package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.ast.untyped.*;
import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.zwobble.clunk.types.Types.metaType;
import static org.zwobble.clunk.util.Lists.last;

public class TypeChecker {
    private static TypedParamNode typeCheckParam(
        UntypedParamNode node,
        TypeCheckerContext context
    ) {
        return new TypedParamNode(
            node.name(),
            typeCheckTypeLevelExpressionNode(node.type(), context),
            node.source()
        );
    }

    private static TypedExpressionNode typeCheckAdd(UntypedAddNode node, TypeCheckerContext context) {
        var left = typeCheckExpression(node.left(), context);
        var right = typeCheckExpression(node.right(), context);

        expectExpressionType(left, Types.INT);
        expectExpressionType(right, Types.INT);

        return new TypedIntAddNode(
            left,
            right,
            node.source()
        );
    }

    private static List<TypedExpressionNode> typeCheckArgs(SignatureNonGeneric signature, UntypedCallNode node, TypeCheckerContext context) {
        if (node.positionalArgs().size() != signature.positionalParams().size()) {
            throw new WrongNumberOfArgumentsError(
                signature.positionalParams().size(),
                node.positionalArgs().size(),
                node.source()
            );
        }

        var typedPositionalArgs = new ArrayList<TypedExpressionNode>();

        for (var argIndex = 0; argIndex < signature.positionalParams().size(); argIndex++) {
            var paramType = signature.positionalParams().get(argIndex);
            var untypedArgNode = node.positionalArgs().get(argIndex);
            var typedArgNode = typeCheckExpression(untypedArgNode, paramType, context);

            typedPositionalArgs.add(typedArgNode);
        }
        return typedPositionalArgs;
    }

    private static TypeCheckFunctionStatementResult<TypedFunctionStatementNode> typeCheckBlankLineInFunction(
        UntypedBlankLineNode node,
        TypeCheckerContext context
    ) {
        return TypeCheckFunctionStatementResult.neverReturns(new TypedBlankLineNode(node.source()), context);
    }

    private static TypeCheckNamespaceStatementResult typeCheckBlankLineInNamespace(
        UntypedBlankLineNode node
    ) {
        return new TypeCheckNamespaceStatementResult(
            List.of(),
            context -> new TypedBlankLineNode(node.source()),
            () -> Optional.empty()
        );
    }

    private static TypedExpressionNode typeCheckBoolLiteral(UntypedBoolLiteralNode node) {
        return new TypedBoolLiteralNode(node.value(), node.source());
    }

    private static TypedExpressionNode typeCheckCall(UntypedCallNode node, TypeCheckerContext context) {
        var receiver = typeCheckExpression(node.receiver(), context);
        var signature = Signatures.toSignature(receiver.type(), context, receiver.source());

        var typeCheckTypeArgsResult = typeCheckTypeArgs(signature, node, context);
        var signatureNonGeneric = typeCheckTypeArgsResult.signatureNonGeneric;

        var typedPositionalArgs = typeCheckArgs(signatureNonGeneric, node, context);

        return switch (signatureNonGeneric.type()) {
            case ConstructorType ignored -> new TypedCallConstructorNode(
                receiver,
                typedPositionalArgs,
                signatureNonGeneric.returnType(),
                node.source()
            );
            case MethodType ignored -> {
                var memberAccess = (TypedMemberAccessNode) receiver;
                yield new TypedCallMethodNode(
                    memberAccess.receiver(),
                    memberAccess.memberName(),
                    typedPositionalArgs,
                    signatureNonGeneric.returnType(),
                    node.source()
                );
            }
            case StaticFunctionType staticFunctionType -> new TypedCallStaticFunctionNode(
                receiver,
                typedPositionalArgs,
                staticFunctionType,
                node.source()
            );
        };
    }

    private static TypedExpressionNode typeCheckCastUnsafe(
        UntypedCastUnsafeNode node,
        TypeCheckerContext context
    ) {
        var typedExpression = typeCheckExpression(node.expression(), context);
        var typedTypeExpression = typeCheckTypeLevelExpressionNode(node.typeExpression(), context);
        var type = typedTypeLevelExpressionToType(typedTypeExpression);

        return new TypedCastUnsafeNode(
            typedExpression,
            typedTypeExpression,
            type,
            node.source()
        );
    }

    private static TypeCheckFunctionStatementResult<TypedConditionalBranchNode> typeCheckConditionalBranch(
        UntypedConditionalBranchNode node,
        TypeCheckerContext context
    ) {
        var typedConditionNode = typeCheckExpression(node.condition(), context);
        expectExpressionType(typedConditionNode, Types.BOOL);

        var bodyContext = context;
        var bodyPrefix = new ArrayList<TypedFunctionStatementNode>();
        if (
            typedConditionNode instanceof TypedInstanceOfNode typedInstanceOfNode
            && typedInstanceOfNode.expression() instanceof TypedReferenceNode typedInstanceOfReferenceNode
        ) {
            var narrowedType = (StructuredType) typedInstanceOfNode.typeExpression().value();
            var variableName = typedInstanceOfReferenceNode.name();
            bodyContext = bodyContext.updateLocal(variableName, narrowedType, typedInstanceOfNode.source());
            bodyPrefix.add(new TypedTypeNarrowNode(variableName, narrowedType, typedInstanceOfNode.source()));
        }

        var typeCheckBodyResults = typeCheckFunctionStatements(node.body(), bodyContext);

        return typeCheckBodyResults.map(body -> new TypedConditionalBranchNode(
            typedConditionNode,
            Stream.concat(bodyPrefix.stream(), body.stream()).toList(),
            node.source()
        ));
    }

    private static TypedTypeLevelExpressionNode typeCheckConstructedTypeNode(
        UntypedConstructedTypeNode node,
        TypeCheckerContext context
    ) {
        var typedReceiverNode = typeCheckTypeLevelExpressionNode(node.receiver(), context);
        var receiverType = typedReceiverNode.value();

        if (receiverType instanceof TypeConstructor typeConstructor) {
            var typedArgNodes = IntStream.range(0, node.args().size())
                .mapToObj(argIndex -> {
                    var arg = node.args().get(argIndex);
                    var param = typeConstructor.param(argIndex);
                    var argType = typeCheckTypeLevelExpressionNode(arg, context);
                    return new TypedConstructedTypeNode.Arg(argType, param.variance());
                })
                .toList();
            var args = typedArgNodes.stream()
                .map(arg -> typedTypeLevelExpressionToType(arg.type()))
                .toList();
            var constructedType = Types.construct(typeConstructor, args);

            return new TypedConstructedTypeNode(typedReceiverNode, typedArgNodes, constructedType, node.source());
        }

        throw new UnexpectedTypeError(
            TypeConstructorTypeSet.INSTANCE,
            Types.typeLevelValueType(receiverType),
            node.receiver().source()
        );
    }

    private static TypeCheckNamespaceStatementResult typeCheckEnum(
        UntypedEnumNode node
    ) {
        var typeBox = new Box<EnumType>();

        return new TypeCheckNamespaceStatementResult(
            List.of(
                new PendingTypeCheck(
                    TypeCheckerPhase.DEFINE_TYPES,
                    context -> {
                        var type = new EnumType(context.namespaceName(), node.name(), node.members());
                        typeBox.set(type);
                        return context.addLocal(node.name(), Types.metaType(type), node.source());
                    }
                )
            ),
            context -> new TypedEnumNode(typeBox.get(), node.source()),
            () -> Optional.of(Map.entry(node.name(), Types.metaType(typeBox.get())))
        );
    }

    private static TypedExpressionNode typeCheckEquals(UntypedEqualsNode node, TypeCheckerContext context) {
        var left = typeCheckExpression(node.left(), context);
        var right = typeCheckExpression(node.right(), context);

        if (left.type().equals(Types.INT)) {
            expectExpressionType(right, Types.INT);
            return new TypedIntEqualsNode(left, right, node.source());
        }

        // TODO: don't assume strings if not ints
        expectExpressionType(left, Types.STRING);
        expectExpressionType(right, Types.STRING);

        return new TypedStringEqualsNode(
            left,
            right,
            node.source()
        );
    }

    public static TypedExpressionNode typeCheckExpression(
        UntypedExpressionNode node,
        Type expected,
        TypeCheckerContext context
    ) {
        var typed = typeCheckExpression(node, context);

        if (expected instanceof FunctionType && typed.type() instanceof StaticFunctionType staticFunctionType) {
            var functionType = new FunctionType(
                staticFunctionType.positionalParams(),
                staticFunctionType.returnType()
            );
            typed = new TypedStaticMethodToFunctionNode(typed, functionType);
        }

        var argType = typed.type();
        if (!context.isSubType(argType, expected)) {
            throw new UnexpectedTypeError(expected, argType, node.source());
        }

        return typed;
    }

    public static TypedExpressionNode typeCheckExpression(
        UntypedExpressionNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedExpressionNode.Visitor<TypedExpressionNode>() {
            @Override
            public TypedExpressionNode visit(UntypedAddNode node) {
                return typeCheckAdd(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedBoolLiteralNode node) {
                return typeCheckBoolLiteral(node);
            }

            @Override
            public TypedExpressionNode visit(UntypedCallNode node) {
                return typeCheckCall(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedCastUnsafeNode node) {
                return typeCheckCastUnsafe(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedEqualsNode node) {
                return typeCheckEquals(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedIndexNode node) {
                return typeCheckIndex(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedInstanceOfNode node) {
                return typeCheckInstanceOf(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedIntLiteralNode node) {
                return typeCheckIntLiteral(node);
            }

            @Override
            public TypedExpressionNode visit(UntypedListLiteralNode node) {
                return typeCheckListLiteral(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedLogicalAndNode node) {
                return typeCheckLogicalAnd(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedLogicalNotNode node) {
                return typeCheckLogicalNot(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedLogicalOrNode node) {
                return typeCheckLogicalOr(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedMemberAccessNode node) {
                return typeCheckMemberAccess(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedReferenceNode node) {
                return typeCheckReference(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedStringLiteralNode node) {
                return typeCheckStringLiteral(node);
            }
        });
    }

    private static TypeCheckFunctionStatementResult<TypedFunctionStatementNode> typeCheckExpressionStatement(
        UntypedExpressionStatementNode node,
        TypeCheckerContext context
    ) {
        var typedExpression = typeCheckExpression(node.expression(), context);
        var typedStatement = new TypedExpressionStatementNode(typedExpression, node.source());
        return TypeCheckFunctionStatementResult.neverReturns(typedStatement, context);
    }

    private static TypeCheckFunctionStatementResult<TypedFunctionStatementNode> typeCheckForEach(
        UntypedForEachNode node,
        TypeCheckerContext context
    ) {
        var typedIterable = typeCheckExpression(node.iterable(), context);
        var iterableType = typedIterable.type();

        if (!(iterableType instanceof ConstructedType iterableTypeList)) {
            throw new UnexpectedTypeError(
                Types.LIST_CONSTRUCTOR.genericType(),
                iterableType,
                node.source()
            );
        }

        var targetType = iterableTypeList.args().get(0);
        var bodyContext = context.addLocal(node.targetName(), targetType, node.source());
        var typeCheckBodyResult = typeCheckFunctionStatements(node.body(), bodyContext);

        var typedNode = new TypedForEachNode(
            node.targetName(),
            targetType,
            typedIterable,
            typeCheckBodyResult.value(),
            node.source()
        );

        return new TypeCheckFunctionStatementResult<>(
            typedNode,
            typeCheckBodyResult.returnBehaviour() == ReturnBehaviour.NEVER ? ReturnBehaviour.NEVER : ReturnBehaviour.SOMETIMES,
            typeCheckBodyResult.returnType(),
            context
        );
    }

    private static TypeCheckNamespaceStatementResult typeCheckFunction(
        UntypedFunctionNode node
    ) {
        var functionTypeBox = new Box<StaticFunctionType>();
        var typedParamNodesBox = new Box<List<TypedParamNode>>();
        var typedReturnTypeNodeBox = new Box<TypedTypeLevelExpressionNode>();
        var typedBodyBox = new Box<List<TypedFunctionStatementNode>>();

        return new TypeCheckNamespaceStatementResult(
            List.of(
                new PendingTypeCheck(
                    TypeCheckerPhase.DEFINE_FUNCTIONS,
                    context -> {
                        var typedParamNodes = node.params().stream().map(param -> typeCheckParam(param, context)).toList();
                        typedParamNodesBox.set(typedParamNodes);
                        var paramTypes = typedParamNodes.stream()
                            .map(param -> typedTypeLevelExpressionToType(param.type()))
                            .toList();
                        var typedReturnTypeNode = typeCheckTypeLevelExpressionNode(node.returnType(), context);
                        typedReturnTypeNodeBox.set(typedReturnTypeNode);
                        var returnType = typedTypeLevelExpressionToType(typedReturnTypeNode);

                        var type = new StaticFunctionType(
                            context.namespaceName(),
                            node.name(),
                            paramTypes,
                            returnType,
                            Visibility.PUBLIC
                        );
                        functionTypeBox.set(type);
                        return context.addLocal(node.name(), type, node.source());
                    }
                ),

                new PendingTypeCheck(
                    TypeCheckerPhase.TYPE_CHECK_BODIES,
                    context -> {
                        var functionType = functionTypeBox.get();
                        var typedParamNodes = typedParamNodesBox.get();

                        var bodyContext = context.enterFunction(functionType.returnType());
                        for (var typedParamNode : typedParamNodes) {
                            bodyContext = bodyContext.addLocal(
                                typedParamNode.name(),
                                typedTypeLevelExpressionToType(typedParamNode.type()),
                                typedParamNode.source()
                            );
                        }

                        var typeCheckStatementsResult = typeCheckFunctionBody(
                            node.body(),
                            node.source(),
                            bodyContext
                        );
                        typedBodyBox.set(typeCheckStatementsResult.value());

                        return context;
                    }
                )
            ),
            context -> {
                var functionType = functionTypeBox.get();
                var typedParamNodes = typedParamNodesBox.get();
                var typedReturnTypeNode = typedReturnTypeNodeBox.get();

                var bodyContext = context.enterFunction(functionType.returnType());
                for (var typedParamNode : typedParamNodes) {
                    bodyContext = bodyContext.addLocal(
                        typedParamNode.name(),
                        typedTypeLevelExpressionToType(typedParamNode.type()),
                        typedParamNode.source()
                    );
                }

                return new TypedFunctionNode(
                    node.name(),
                    typedParamNodes,
                    typedReturnTypeNode,
                    typedBodyBox.get(),
                    node.source()
                );
            },
            () -> Optional.of(Map.entry(
                node.name(),
                functionTypeBox.get()
            ))
        );
    }

    private static TypeCheckFunctionStatementResult<List<TypedFunctionStatementNode>> typeCheckFunctionBody(
        List<UntypedFunctionStatementNode> body,
        Source source,
        TypeCheckerContext context
    ) {
        var typeCheckStatementsResult = typeCheckFunctionStatements(
            body,
            context
        );

        if (!typeCheckStatementsResult.alwaysReturns() && !context.returnType().get().equals(Types.UNIT)) {
            throw new MissingReturnError(source);
        }
        return typeCheckStatementsResult;
    }

    public static TypeCheckFunctionStatementResult<List<TypedFunctionStatementNode>> typeCheckFunctionStatement(
        UntypedFunctionStatementNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedFunctionStatementNode.Visitor<>() {
            @Override
            public TypeCheckFunctionStatementResult<List<TypedFunctionStatementNode>> visit(UntypedBlankLineNode node) {
                return typeCheckBlankLineInFunction(node, context).map(List::of);
            }

            @Override
            public TypeCheckFunctionStatementResult<List<TypedFunctionStatementNode>> visit(UntypedExpressionStatementNode node) {
                return typeCheckExpressionStatement(node, context).map(List::of);
            }

            @Override
            public TypeCheckFunctionStatementResult<List<TypedFunctionStatementNode>> visit(UntypedForEachNode node) {
                return typeCheckForEach(node, context).map(List::of);
            }

            @Override
            public TypeCheckFunctionStatementResult<List<TypedFunctionStatementNode>> visit(UntypedIfStatementNode node) {
                return typeCheckIfStatement(node, context).map(List::of);
            }

            @Override
            public TypeCheckFunctionStatementResult<List<TypedFunctionStatementNode>> visit(UntypedReturnNode node) {
                return typeCheckReturn(node, context).map(List::of);
            }

            @Override
            public TypeCheckFunctionStatementResult<List<TypedFunctionStatementNode>> visit(UntypedSingleLineCommentNode node) {
                return typeCheckSingleLineCommentInFunction(node, context).map(List::of);
            }

            @Override
            public TypeCheckFunctionStatementResult<List<TypedFunctionStatementNode>> visit(UntypedSwitchNode node) {
                return typeCheckSwitch(node, context).map(List::of);
            }

            @Override
            public TypeCheckFunctionStatementResult<List<TypedFunctionStatementNode>> visit(UntypedVarNode node) {
                return typeCheckVar(node, context).map(List::of);
            }
        });
    }

    private static TypeCheckFunctionStatementResult<List<TypedFunctionStatementNode>> typeCheckFunctionStatements(
        List<UntypedFunctionStatementNode> body,
        TypeCheckerContext context
    ) {
        var typedStatements = new ArrayList<TypedFunctionStatementNode>();
        var returnBehaviour = ReturnBehaviour.NEVER;
        var returnType = Types.NOTHING;

        for (var statement : body) {
            var statementResult = typeCheckFunctionStatement(statement, context);
            context = statementResult.context();
            typedStatements.addAll(statementResult.value());

            // TODO: test this!
            if (statementResult.returnBehaviour().equals(ReturnBehaviour.ALWAYS)) {
                returnBehaviour = ReturnBehaviour.ALWAYS;
            } else if (statementResult.returnBehaviour().equals(ReturnBehaviour.SOMETIMES) && returnBehaviour.equals(ReturnBehaviour.NEVER)) {
                returnBehaviour = ReturnBehaviour.SOMETIMES;
            }
            returnType = Types.unify(returnType, statementResult.returnType());
        }

        return new TypeCheckFunctionStatementResult<>(typedStatements, returnBehaviour, returnType, context);
    }

    private static TypeCheckFunctionStatementResult<TypedFunctionStatementNode> typeCheckIfStatement(
        UntypedIfStatementNode node,
        TypeCheckerContext context
    ) {
        var typedConditionalBranches = new ArrayList<TypedConditionalBranchNode>();
        var returnBehaviours = new ArrayList<ReturnBehaviour>();
        var returnType = Types.NOTHING;

        var bodyContext = context;
        var bodyPrefix = new ArrayList<TypedFunctionStatementNode>();

        for (var untypedConditionalBranch : node.conditionalBranches()) {
            var result = typeCheckConditionalBranch(untypedConditionalBranch, bodyContext)
                .map(typedConditionalBranch -> typedConditionalBranch.withBody(
                    Stream.concat(bodyPrefix.stream(), typedConditionalBranch.body().stream()).toList()
                ));
            typedConditionalBranches.add(result.value());
            returnBehaviours.add(result.returnBehaviour());
            returnType = Types.unify(returnType, result.returnType());

            if (
                result.value().condition() instanceof TypedLogicalNotNode typedConditionNotNode &&
                typedConditionNotNode.operand() instanceof TypedInstanceOfNode typedInstanceOfNode &&
                typedInstanceOfNode.expression() instanceof TypedReferenceNode typedReferenceNode
            ) {
                var narrowedType = (StructuredType) typedInstanceOfNode.typeExpression().value();
                var variableName = typedReferenceNode.name();
                bodyContext = bodyContext.updateLocal(variableName, narrowedType, typedConditionNotNode.source());
                bodyPrefix.add(new TypedTypeNarrowNode(variableName, narrowedType, typedConditionNotNode.source()));

                if (result.alwaysReturns()) {
                    context = context.updateLocal(variableName, narrowedType, typedConditionNotNode.source());
                }
            }
        }

        var typeCheckElseResult = typeCheckFunctionStatements(node.elseBody(), bodyContext)
            .map(typedElseBody -> Stream.concat(bodyPrefix.stream(), typedElseBody.stream()).toList());

        returnBehaviours.add(typeCheckElseResult.returnBehaviour());
        returnType = Types.unify(returnType, typeCheckElseResult.returnType());

        var typedNode = new TypedIfStatementNode(
            typedConditionalBranches,
            typeCheckElseResult.value(),
            node.source()
        );

        return new TypeCheckFunctionStatementResult<>(
            typedNode,
            ReturnBehaviours.or(returnBehaviours),
            returnType,
            context
        );
    }

    public record TypeCheckImportResult(TypedImportNode node, TypeCheckerContext context) {
    }

    public static TypeCheckImportResult typeCheckImport(
        UntypedImportNode import_,
        TypeCheckerContext context
    ) {

        var type = context.typeOfNamespace(import_.namespaceName());
        if (type.isEmpty()) {
            throw new SourceError("unknown namespace: " + import_.namespaceName(), import_.source());
        }

        if (import_.fieldName().isPresent()) {
            var fieldName = import_.fieldName().get();

            var importType = type.get().fields().get(fieldName);
            if (importType == null) {
                throw new SourceError(
                    "unknown field " + fieldName + " on namespace " + import_.namespaceName(),
                    import_.source()
                );
            }

            var newContext = context.addLocal(fieldName, importType, import_.source());

            var typedNode = new TypedImportNode(import_.namespaceName(), import_.fieldName(), importType, import_.source());

            return new TypeCheckImportResult(typedNode, newContext);
        } else {
            var variableName = last(import_.namespaceName().parts());
            var newContext = context.addLocal(variableName, type.get(), import_.source());

            var typedNode = new TypedImportNode(import_.namespaceName(), import_.fieldName(), type.get(), import_.source());

            return new TypeCheckImportResult(typedNode, newContext);
        }
    }

    private static TypedExpressionNode typeCheckIndex(UntypedIndexNode node, TypeCheckerContext context) {
        var typedReceiverNode = typeCheckExpression(node.receiver(), context);
        var typedIndexNode = typeCheckExpression(node.index(), context);

        var receiverType = typedReceiverNode.type();
        if (receiverType instanceof ConstructedType listType && listType.constructor().equals(Types.LIST_CONSTRUCTOR)) {
            expectExpressionType(typedIndexNode, Types.INT);
            return new TypedIndexNode(
                typedReceiverNode,
                typedIndexNode,
                listType.args().get(0),
                node.source()
            );
        } else {
            throw new UnexpectedTypeError(IndexableTypeSet.INSTANCE, typedReceiverNode.type(), node.source());
        }
    }

    private static TypedExpressionNode typeCheckInstanceOf(
        UntypedInstanceOfNode node,
        TypeCheckerContext context
    ) {
        var typedExpression = typeCheckExpression(node.expression(), context);
        var interfaceType = verifyIsSealedInterfaceType(typedExpression);

        var typedTypeExpression = typeCheckTypeLevelExpressionNode(node.typeExpression(), context);
        var typeExpressionType = typedTypeLevelExpressionToType(typedTypeExpression);

        var subtypes = context.sealedInterfaceCases(interfaceType);
        if (!subtypes.contains(typeExpressionType)) {
            throw new InvalidCaseTypeError(
                typedExpression.type(),
                typeExpressionType,
                typedTypeExpression.source()
            );
        }

        return new TypedInstanceOfNode(
            typedExpression,
            typedTypeExpression,
            node.source()
        );
    }

    private static TypeCheckNamespaceStatementResult typeCheckInterface(UntypedInterfaceNode node) {
        var interfaceTypeBox = new Box<InterfaceType>();

        return new TypeCheckNamespaceStatementResult(
            List.of(
                new PendingTypeCheck(
                    TypeCheckerPhase.DEFINE_TYPES,
                    context -> {
                        var interfaceType = new InterfaceType(context.namespaceName(), node.name());
                        interfaceTypeBox.set(interfaceType);
                        return context.addLocal(node.name(), metaType(interfaceType), node.source());
                    }
                )
            ),
            context ->  new TypedInterfaceNode(node.name(), interfaceTypeBox.get(), node.source()),
            () -> Optional.of(Map.entry(
                node.name(),
                Types.metaType(interfaceTypeBox.get())
            ))
        );
    }

    private static TypedExpressionNode typeCheckIntLiteral(UntypedIntLiteralNode node) {
        return new TypedIntLiteralNode(node.value(), node.source());
    }

    private static TypedExpressionNode typeCheckListLiteral(UntypedListLiteralNode node, TypeCheckerContext context) {
        var elements = node.elements().stream()
            .map(element -> typeCheckExpression(element, context))
            .toList();

        var elementTypes = elements.stream()
            .map(element -> element.type())
            .distinct()
            .toList();

        var elementType = Types.unify(elementTypes, Types.NOTHING);

        return new TypedListLiteralNode(
            elements,
            elementType,
            node.source()
        );
    }

    private static TypedExpressionNode typeCheckLogicalAnd(UntypedLogicalAndNode node, TypeCheckerContext context) {
        var left = typeCheckExpression(node.left(), context);
        var right = typeCheckExpression(node.right(), context);

        expectExpressionType(left, Types.BOOL);
        expectExpressionType(right, Types.BOOL);

        return new TypedLogicalAndNode(
            left,
            right,
            node.source()
        );
    }

    private static TypedExpressionNode typeCheckLogicalNot(UntypedLogicalNotNode node, TypeCheckerContext context) {
        var operand = typeCheckExpression(node.operand(), context);

        expectExpressionType(operand, Types.BOOL);

        return new TypedLogicalNotNode(operand, node.source());
    }

    private static TypedExpressionNode typeCheckLogicalOr(UntypedLogicalOrNode node, TypeCheckerContext context) {
        var left = typeCheckExpression(node.left(), context);
        var right = typeCheckExpression(node.right(), context);

        expectExpressionType(left, Types.BOOL);
        expectExpressionType(right, Types.BOOL);

        return new TypedLogicalOrNode(
            left,
            right,
            node.source()
        );
    }

    private static TypedExpressionNode typeCheckMemberAccess(UntypedMemberAccessNode node, TypeCheckerContext context) {
        var typedReceiverNode = typeCheckExpression(node.receiver(), context);
        var recordType = typedReceiverNode.type();
        var memberType = context.memberType(recordType, node.memberName());

        if (memberType.isEmpty()) {
            throw new UnknownMemberError(recordType, node.memberName(), node.operatorSource());
        }

        return new TypedMemberAccessNode(
            typedReceiverNode,
            node.memberName(),
            memberType.get(),
            node.operatorSource(),
            node.source()
        );
    }

    public static TypeCheckResult<TypedNamespaceNode> typeCheckNamespace(
        UntypedNamespaceNode node,
        TypeCheckerContext context
    ) {
        context = context.enterNamespace(node.name());

        var typedImports = new ArrayList<TypedImportNode>();
        for (var import_ : node.imports()) {
            var typeCheckImportResult = typeCheckImport(import_, context);
            context = typeCheckImportResult.context;
            typedImports.add(typeCheckImportResult.node);
        }

        var typeCheckBodyResult = typeCheckNamespaceStatements(node.statements(), context);
        context = typeCheckBodyResult.context;

        var namespaceType = new NamespaceType(node.name(), typeCheckBodyResult.fieldTypes);

        var typedNode = new TypedNamespaceNode(
            node.name(),
            typedImports,
            typeCheckBodyResult.typedBody,
            namespaceType,
            node.sourceType(),
            node.source()
        );

        return new TypeCheckResult<>(typedNode, context.updateNamespaceType(namespaceType).leave());
    }

    private record TypeCheckNamespaceStatementsResult(
        List<TypedNamespaceStatementNode> typedBody,
        Map<String, Type> fieldTypes,
        TypeCheckerContext context
    ) {
    }

    private static TypeCheckNamespaceStatementsResult typeCheckNamespaceStatements(
        List<UntypedNamespaceStatementNode> statements,
        TypeCheckerContext context
    ) {
        var typeCheckResults = new ArrayList<TypeCheckNamespaceStatementResult>();
        for (var statement : statements) {
            var result = typeCheckNamespaceStatement(statement);
            typeCheckResults.add(result);
        }

        for (var phase : TypeCheckerPhase.values()) {
            for (var typeCheckResult : typeCheckResults) {
                for (var pendingTypeCheck : typeCheckResult.pendingTypeChecks()) {
                    if (pendingTypeCheck.phase().equals(phase)) {
                        context = pendingTypeCheck.typeCheck(context);
                    }
                }
            }
        }

        var typedBody = new ArrayList<TypedNamespaceStatementNode>();
        var fieldTypes = new HashMap<String, Type>();
        for (var result : typeCheckResults) {
            typedBody.add(result.value(context));
            var fieldType = result.fieldType();
            if (fieldType.isPresent()) {
                fieldTypes.put(fieldType.get().getKey(), fieldType.get().getValue());
            }
        }

        return new TypeCheckNamespaceStatementsResult(typedBody, fieldTypes, context);
    }

    public static TypeCheckNamespaceStatementResult typeCheckNamespaceStatement(
        UntypedNamespaceStatementNode node
    ) {
        return node.accept(new UntypedNamespaceStatementNode.Visitor<TypeCheckNamespaceStatementResult>() {
            @Override
            public TypeCheckNamespaceStatementResult visit(UntypedBlankLineNode node) {
                return typeCheckBlankLineInNamespace(node);
            }

            @Override
            public TypeCheckNamespaceStatementResult visit(UntypedEnumNode node) {
                return typeCheckEnum(node);
            }

            @Override
            public TypeCheckNamespaceStatementResult visit(UntypedFunctionNode node) {
                return typeCheckFunction(node);
            }

            @Override
            public TypeCheckNamespaceStatementResult visit(UntypedInterfaceNode node) {
                return typeCheckInterface(node);
            }

            @Override
            public TypeCheckNamespaceStatementResult visit(UntypedRecordNode node) {
                return typeCheckRecord(node);
            }

            @Override
            public TypeCheckNamespaceStatementResult visit(UntypedSingleLineCommentNode node) {
                return typeCheckSingleLineCommentInNamespace(node);
            }

            @Override
            public TypeCheckNamespaceStatementResult visit(UntypedTestNode node) {
                return typeCheckTest(node);
            }

            @Override
            public TypeCheckNamespaceStatementResult visit(UntypedTestSuiteNode node) {
                return typeCheckTestSuite(node);
            }
        });
    }

    private static TypeCheckRecordBodyDeclarationResult typeCheckProperty(
        UntypedPropertyNode node,
        TypeCheckerContext initialContext
    ) {
        var typedTypeNode = typeCheckTypeLevelExpressionNode(node.type(), initialContext);
        var type = typedTypeLevelExpressionToType(typedTypeNode);
        return new TypeCheckRecordBodyDeclarationResult(
            Map.of(node.name(), type),
            bodyContext -> {
                var typeCheckBodyResult = typeCheckFunctionBody(
                    node.body(),
                    node.source(),
                    bodyContext.enterFunction(type)
                );

                return new TypedPropertyNode(
                    node.name(),
                    typedTypeNode,
                    typeCheckBodyResult.value(),
                    node.source()
                );
            },
            node.source()
        );
    }

    private static TypeCheckNamespaceStatementResult typeCheckRecord(
        UntypedRecordNode node
    ) {
        var recordTypeBox = new Box<RecordType>();
        var typedSupertypeNodesBox = new Box<List<TypedTypeLevelExpressionNode>>();
        var typedRecordFieldNodesBox = new Box<List<TypedRecordFieldNode>>();
        var memberTypesBox = new Box<Map<String, Type>>();
        var typeCheckBodyDeclarationResultsBox = new Box<List<TypeCheckRecordBodyDeclarationResult>>();
        var typedBodyDeclarationsBox = new Box<List<? extends TypedRecordBodyDeclarationNode>>();

        return new TypeCheckNamespaceStatementResult(
            List.of(
                new PendingTypeCheck(
                    TypeCheckerPhase.DEFINE_TYPES,
                    context -> {
                        var recordType = new RecordType(context.namespaceName(), node.name());
                        recordTypeBox.set(recordType);
                        return context.addLocal(node.name(), metaType(recordType), node.source());
                    }
                ),
                new PendingTypeCheck(
                    TypeCheckerPhase.GENERATE_TYPE_INFO,
                    context -> {
                        var recordType = recordTypeBox.get();

                        var typedRecordFieldNodes = node.fields().stream()
                            .map(field -> typeCheckRecordField(field, context))
                            .collect(Collectors.toList());
                        typedRecordFieldNodesBox.set(typedRecordFieldNodes);

                        var typedSupertypeNodes = node.supertypes().stream()
                            .map(untypedSupertypeNode -> {
                                var typedSupertypeNode = typeCheckTypeLevelExpressionNode(untypedSupertypeNode, context);
                                // TODO: handle non-type type-level values
                                if (typedSupertypeNode.value() instanceof InterfaceType supertype) {
                                    if (!supertype.namespaceName().equals(recordType.namespaceName())) {
                                        throw new CannotExtendSealedInterfaceFromDifferentNamespaceError(untypedSupertypeNode.source());
                                    }
                                } else {
                                    throw new CannotExtendFinalTypeError(untypedSupertypeNode.source());
                                }
                                return typedSupertypeNode;
                            })
                            .toList();
                        typedSupertypeNodesBox.set(typedSupertypeNodes);

                        var typeCheckBodyDeclarationResults = node.body().stream()
                            .map(declaration -> typeCheckRecordBodyDeclaration(declaration, context))
                            .toList();
                        typeCheckBodyDeclarationResultsBox.set(typeCheckBodyDeclarationResults);

                        var memberTypesBuilder = new MemberTypesBuilder();
                        for (var typedFieldNode : typedRecordFieldNodes) {
                            memberTypesBuilder.add(
                                typedFieldNode.name(),
                                typedTypeLevelExpressionToType(typedFieldNode.type()),
                                typedFieldNode.source()
                            );
                        }
                        for (var typeCheckDeclarationResult : typeCheckBodyDeclarationResults) {
                            memberTypesBuilder.addAll(typeCheckDeclarationResult.memberTypes(), typeCheckDeclarationResult.source());
                        }

                        var constructorArgs = typedRecordFieldNodes.stream()
                            .map(fieldNode -> typedTypeLevelExpressionToType(fieldNode.type()))
                            .toList();
                        var newContext = context.addConstructorType(new ConstructorType(
                            recordType.namespaceName(),
                            Optional.empty(),
                            constructorArgs,
                            recordType,
                            Visibility.PUBLIC
                        ));
                        var memberTypes = memberTypesBuilder.build();
                        memberTypesBox.set(memberTypes);
                        newContext = newContext.addMemberTypes(recordType, memberTypes);
                        for (var typedSupertypeNode : typedSupertypeNodes) {
                            // TODO: handle type-level values that aren't types
                            var interfaceType = (InterfaceType) typedSupertypeNode.value();
                            newContext = newContext
                                .addSubtypeRelation(recordType, interfaceType)
                                .addSealedInterfaceCase(interfaceType, recordType);
                        }
                        return newContext;
                    }
                ),
                new PendingTypeCheck(
                    TypeCheckerPhase.TYPE_CHECK_BODIES,
                    context -> {
                        var bodyContext = context.enterRecordBody(memberTypesBox.get());
                        var typedBody = typeCheckBodyDeclarationResultsBox.get().stream()
                            .map(result -> result.value(bodyContext))
                            .toList();
                        typedBodyDeclarationsBox.set(typedBody);
                        return context;
                    }
                )
            ),
            context -> new TypedRecordNode(
                node.name(),
                recordTypeBox.get(),
                typedRecordFieldNodesBox.get(),
                typedSupertypeNodesBox.get(),
                typedBodyDeclarationsBox.get(),
                node.source()
            ),
            () -> Optional.of(Map.entry(node.name(), Types.metaType(recordTypeBox.get())))
        );
    }

    public static TypeCheckRecordBodyDeclarationResult typeCheckRecordBodyDeclaration(
        UntypedRecordBodyDeclarationNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedRecordBodyDeclarationNode.Visitor<TypeCheckRecordBodyDeclarationResult>() {
            @Override
            public TypeCheckRecordBodyDeclarationResult visit(UntypedBlankLineNode node) {
                return new TypeCheckRecordBodyDeclarationResult(
                    Map.of(),
                    context -> new TypedBlankLineNode(node.source()),
                    node.source()
                );
            }

            @Override
            public TypeCheckRecordBodyDeclarationResult visit(UntypedPropertyNode node) {
                return typeCheckProperty(node, context);
            }

            @Override
            public TypeCheckRecordBodyDeclarationResult visit(UntypedSingleLineCommentNode node) {
                return new TypeCheckRecordBodyDeclarationResult(
                    Map.of(),
                    context -> typeCheckSingleLineComment(node),
                    node.source()
                );
            }
        });
    }

    private static TypedRecordFieldNode typeCheckRecordField(
        UntypedRecordFieldNode node,
        TypeCheckerContext context
    ) {
        return new TypedRecordFieldNode(
            node.name(),
            typeCheckTypeLevelExpressionNode(node.type(), context),
            node.source()
        );
    }

    private static TypedReferenceNode typeCheckReference(UntypedReferenceNode node, TypeCheckerContext context) {
        var variable = context.lookup(node.name(), node.source());
        if (variable.isMember()) {
            return new TypedMemberReferenceNode(node.name(), variable.type(), node.source());
        } else {
            return new TypedLocalReferenceNode(node.name(), variable.type(), node.source());
        }
    }

    private static TypeCheckFunctionStatementResult<TypedFunctionStatementNode> typeCheckReturn(UntypedReturnNode node, TypeCheckerContext context) {
        if (context.returnType().isEmpty()) {
            throw new CannotReturnHereError(node.source());
        }

        var returnType = context.returnType().get();
        var expression = typeCheckExpression(node.expression(), returnType, context);

        var typedNode = new TypedReturnNode(expression, node.source());

        return new TypeCheckFunctionStatementResult<>(typedNode, ReturnBehaviour.ALWAYS, returnType, context);
    }

    private static TypedSingleLineCommentNode typeCheckSingleLineComment(UntypedSingleLineCommentNode node) {
        return new TypedSingleLineCommentNode(node.value(), node.source());
    }

    private static TypeCheckFunctionStatementResult<TypedFunctionStatementNode> typeCheckSingleLineCommentInFunction(
        UntypedSingleLineCommentNode node,
        TypeCheckerContext context
    ) {
        return TypeCheckFunctionStatementResult.neverReturns(typeCheckSingleLineComment(node), context);
    }

    private static TypeCheckNamespaceStatementResult typeCheckSingleLineCommentInNamespace(
        UntypedSingleLineCommentNode node
    ) {
        return new TypeCheckNamespaceStatementResult(
            List.of(),
            context -> typeCheckSingleLineComment(node),
            () -> Optional.empty()
        );
    }

    private static TypedExpressionNode typeCheckStringLiteral(UntypedStringLiteralNode node) {
        return new TypedStringLiteralNode(node.value(), node.source());
    }

    private static TypeCheckFunctionStatementResult<TypedFunctionStatementNode> typeCheckSwitch(
        UntypedSwitchNode node,
        TypeCheckerContext context
    ) {
        var typedExpressionNode = typeCheckReference(node.expression(), context);
        var interfaceType = verifyIsSealedInterfaceType(typedExpressionNode);

        var returnBehaviours = new ArrayList<ReturnBehaviour>();
        var returnType = Types.NOTHING;

        var unhandledTypes = new HashSet<>(context.sealedInterfaceCases(interfaceType));

        var typedCaseNodes = new ArrayList<TypedSwitchCaseNode>();
        for (var switchCase : node.cases()) {
            var typedType = typeCheckTypeLevelExpressionNode(switchCase.type(), context);
            var caseType = typedTypeLevelExpressionToType(typedType);
            var bodyContext = context.updateLocal(node.expression().name(), caseType, switchCase.source());
            var typedBody = typeCheckFunctionStatements(switchCase.body(), bodyContext);
            var typedCaseNode = new TypedSwitchCaseNode(typedType, typedBody.value(), node.source());
            typedCaseNodes.add(typedCaseNode);

            returnBehaviours.add(typedBody.returnBehaviour());
            returnType = Types.unify(returnType, typedBody.returnType());

            if (!unhandledTypes.remove(caseType)) {
                throw new InvalidCaseTypeError(typedExpressionNode.type(), caseType, switchCase.source());
            }
        }

        if (!unhandledTypes.isEmpty()) {
            throw new SwitchIsNotExhaustiveError(unhandledTypes.stream().toList(), node.source());
        }

        var returnBehaviour = ReturnBehaviours.or(returnBehaviours);

        if (returnBehaviour.equals(ReturnBehaviour.SOMETIMES)) {
            throw new InconsistentSwitchCaseReturnError(node.source());
        }

        var typedNode = new TypedSwitchNode(
            typedExpressionNode,
            typedCaseNodes,
            returnBehaviour.equals(ReturnBehaviour.ALWAYS) ? Optional.of(returnType) : Optional.empty(),
            node.source()
        );

        return new TypeCheckFunctionStatementResult<>(
            typedNode,
            returnBehaviour,
            returnType,
            context
        );
    }

    private static TypeCheckNamespaceStatementResult typeCheckTest(
        UntypedTestNode node
    ) {

        return new TypeCheckNamespaceStatementResult(
            List.of(),
            context -> {
                var typedStatements = typeCheckFunctionStatements(
                    node.body(),
                    context.enterTest()
                ).value();

                return new TypedTestNode(
                    node.name(),
                    typedStatements,
                    node.source()
                );
            },
            () -> Optional.empty()
        );
    }

    private static TypeCheckNamespaceStatementResult typeCheckTestSuite(UntypedTestSuiteNode node) {
        var typeCheckBodyResultBox = new Box<TypeCheckNamespaceStatementsResult>();

        return new TypeCheckNamespaceStatementResult(
            List.of(
                new PendingTypeCheck(
                    TypeCheckerPhase.TYPE_CHECK_BODIES,
                    context -> {
                        var bodyContext = context.enterTestSuite();

                        var typeCheckBodyResult = typeCheckNamespaceStatements(node.body(), bodyContext);
                        typeCheckBodyResultBox.set(typeCheckBodyResult);

                        return typeCheckBodyResult.context.leave();
                    }
                )
            ),
            context -> new TypedTestSuiteNode(
                node.name(),
                typeCheckBodyResultBox.get().typedBody,
                node.source()
            ),
            () -> Optional.empty()
        );
    }

    private record TypeCheckTypeArgsResult(
        Optional<List<TypedTypeLevelExpressionNode>> nodes,
        SignatureNonGeneric signatureNonGeneric
    ) {
    }

    private static TypeCheckTypeArgsResult typeCheckTypeArgs(
        Signature signature,
        UntypedCallNode node,
        TypeCheckerContext context
    ) {
        switch (signature) {
            case SignatureNonGeneric signatureNonGeneric -> {
                if (node.typeLevelArgs().isEmpty()) {
                    return new TypeCheckTypeArgsResult(Optional.empty(), signatureNonGeneric);
                } else {
                    throw new CannotPassTypeLevelArgsToNonGenericValueError(node.source());
                }
            }
            case SignatureGeneric signatureGeneric -> {
                if (node.typeLevelArgs().isEmpty()) {
                    throw new MissingTypeLevelArgsError(node.source());
                } else if (node.typeLevelArgs().size() != signatureGeneric.typeParams().size()) {
                    throw new WrongNumberOfTypeLevelArgsError(
                        signatureGeneric.typeParams().size(),
                        node.typeLevelArgs().size(),
                        node.source()
                    );
                } else {
                    var typedArgs = node.typeLevelArgs().stream()
                        .map(arg -> typeCheckTypeLevelExpressionNode(arg, context))
                        .toList();

                    var nonGenericType = signatureGeneric.typeArgs(
                        typedArgs.stream()
                            .map(arg -> (Type) arg.value())
                            .toList()
                    );

                    var nonGenericSignature = (SignatureNonGeneric) Signatures.toSignature(nonGenericType, context, node.source());

                    return new TypeCheckTypeArgsResult(Optional.of(typedArgs), nonGenericSignature);
                }
            }
            default -> {
                throw new UnsupportedOperationException();
            }
        }
    }

    public static TypedTypeLevelExpressionNode typeCheckTypeLevelExpressionNode(
        UntypedTypeLevelExpressionNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedTypeLevelExpressionNode.Visitor<TypedTypeLevelExpressionNode>() {
            @Override
            public TypedTypeLevelExpressionNode visit(UntypedConstructedTypeNode node) {
                return typeCheckConstructedTypeNode(node, context);
            }

            @Override
            public TypedTypeLevelExpressionNode visit(UntypedTypeLevelReferenceNode node) {
                return typeCheckTypeLevelReferenceNode(node, context);
            }
        });
    }

    public static TypedTypeLevelExpressionNode typeCheckTypeLevelReferenceNode(
        UntypedTypeLevelReferenceNode node,
        TypeCheckerContext context
    ) {
        var type = resolveTypeLevelValue(node.name(), node.source(), context);
        return new TypedTypeLevelReferenceNode(node.name(), type, node.source());
    }

    private static TypeCheckFunctionStatementResult<TypedFunctionStatementNode> typeCheckVar(
        UntypedVarNode node,
        TypeCheckerContext context
    ) {
        var typedExpression = typeCheckExpression(node.expression(), context);
        var typedNode = new TypedVarNode(
            node.name(),
            typedExpression,
            node.source()
        );
        var updatedContext = context.addLocal(node.name(), typedExpression.type(), node.source());
        return TypeCheckFunctionStatementResult.neverReturns(typedNode, updatedContext);
    }

    private static TypeLevelValue resolveTypeLevelValue(String name, Source source, TypeCheckerContext context) {
        var type = context.typeOf(name, source);
        if (type instanceof TypeLevelValueType) {
            return ((TypeLevelValueType) type).value();
        } else {
            throw new UnexpectedTypeError(TypeLevelValueTypeSet.INSTANCE, type, source);
        }
    }

    private static void expectExpressionType(TypedExpressionNode node, Type type) {
        if (!node.type().equals(type)) {
            throw new UnexpectedTypeError(type, node.type(), node.source());
        }
    }

    private static Type typedTypeLevelExpressionToType(TypedTypeLevelExpressionNode typedTypeExpression) {
        if (typedTypeExpression.value() instanceof Type type) {
            return type;
        } else {
            throw new UnexpectedTypeError(
                MetaTypeTypeSet.INSTANCE,
                Types.typeLevelValueType(typedTypeExpression.value()),
                typedTypeExpression.source()
            );
        }
    }

    private static InterfaceType verifyIsSealedInterfaceType(TypedExpressionNode typedExpressionNode) {
        if (typedExpressionNode.type() instanceof InterfaceType interfaceType) {
            return interfaceType;
        } else {
            throw new UnexpectedTypeError(
                SealedInterfaceTypeSet.INSTANCE,
                typedExpressionNode.type(),
                typedExpressionNode.source()
            );
        }
    }
}
