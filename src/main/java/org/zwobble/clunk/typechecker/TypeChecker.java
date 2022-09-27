package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.ast.untyped.*;
import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.*;

import java.util.*;
import java.util.stream.Collectors;

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

    private static List<TypedExpressionNode> typeCheckArgs(Signature signature, UntypedCallNode node, TypeCheckerContext context) {
        if (node.positionalArgs().size() != signature.positionalParams().size()) {
            throw new WrongNumberOfArgumentsError(
                signature.positionalParams().size(),
                node.positionalArgs().size(),
                node.source()
            );
        }
        var typedPositionalArgs = node.positionalArgs().stream().map(arg -> typeCheckExpression(arg, context)).toList();

        for (var argIndex = 0; argIndex < signature.positionalParams().size(); argIndex++) {
            var paramType = signature.positionalParams().get(argIndex);
            var argNode = typedPositionalArgs.get(argIndex);
            var argType = argNode.type();
            if (!context.isSubType(argType, paramType)) {
                throw new UnexpectedTypeError(paramType, argType, argNode.source());
            }
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
        var signature = Signatures.toSignature(receiver.type(), context);
        var typedPositionalArgs = typeCheckArgs(signature, node, context);

        return switch (signature) {
            case SignatureConstructorRecord signature2 -> new TypedCallConstructorNode(
                receiver,
                typedPositionalArgs,
                signature2.returnType(),
                node.source()
            );
            case SignatureConstructorStringBuilder signature2 -> new TypedCallConstructorNode(
                receiver,
                typedPositionalArgs,
                signature2.returnType(),
                node.source()
            );
            case SignatureMethod signature2 -> {
                var memberAccess = (TypedMemberAccessNode) receiver;
                yield new TypedCallMethodNode(
                    memberAccess.receiver(),
                    memberAccess.memberName(),
                    typedPositionalArgs,
                    signature2.returnType(),
                    node.source()
                );
            }
            case SignatureStaticFunction signature2 -> new TypedCallStaticFunctionNode(
                receiver,
                typedPositionalArgs,
                signature2.type(),
                node.source()
            );
        };
    }

    private static TypeCheckFunctionStatementResult<TypedConditionalBranchNode> typeCheckConditionalBranch(
        UntypedConditionalBranchNode node,
        TypeCheckerContext context
    ) {
        var typedConditionNode = typeCheckExpression(node.condition(), context);

        expectExpressionType(typedConditionNode, Types.BOOL);

        var typeCheckBodyResults = typeCheckFunctionStatements(node.body(), context);

        return typeCheckBodyResults.map(body -> new TypedConditionalBranchNode(
            typedConditionNode,
            body,
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
            var typedArgNodes = node.args().stream()
                .map(arg -> TypedConstructedTypeNode.Arg.covariant(typeCheckTypeLevelExpressionNode(arg, context)))
                .toList();
            // TODO: handle non-type args
            var args = typedArgNodes.stream().map(arg -> (Type) arg.type().value()).toList();
            var constructedType = typeConstructor.call(args);

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
                        var type = new EnumType(context.currentFrame().namespaceName().get(), node.name(), node.members());
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
            public TypedExpressionNode visit(UntypedEqualsNode node) {
                return typeCheckEquals(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedIndexNode node) {
                return typeCheckIndex(node, context);
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
                        // TODO: handle not a type
                        var paramTypes = typedParamNodes.stream().map(param -> (Type) param.type().value()).toList();
                        var typedReturnTypeNode = typeCheckTypeLevelExpressionNode(node.returnType(), context);
                        typedReturnTypeNodeBox.set(typedReturnTypeNode);
                        // TODO: handle not a type
                        var returnType = (Type) typedReturnTypeNode.value();

                        var type = new StaticFunctionType(
                            context.currentFrame().namespaceName().get(),
                            node.name(),
                            paramTypes,
                            returnType
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
                            bodyContext = bodyContext.addLocal(typedParamNode.name(), (Type) typedParamNode.type().value(), typedParamNode.source());
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
                    bodyContext = bodyContext.addLocal(typedParamNode.name(), (Type) typedParamNode.type().value(), typedParamNode.source());
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

    public static TypeCheckFunctionStatementResult<TypedFunctionStatementNode> typeCheckFunctionStatement(
        UntypedFunctionStatementNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedFunctionStatementNode.Visitor<>() {
            @Override
            public TypeCheckFunctionStatementResult<TypedFunctionStatementNode> visit(UntypedBlankLineNode node) {
                return typeCheckBlankLineInFunction(node, context);
            }

            @Override
            public TypeCheckFunctionStatementResult<TypedFunctionStatementNode> visit(UntypedExpressionStatementNode node) {
                return typeCheckExpressionStatement(node, context);
            }

            @Override
            public TypeCheckFunctionStatementResult<TypedFunctionStatementNode> visit(UntypedIfStatementNode node) {
                return typeCheckIfStatement(node, context);
            }

            @Override
            public TypeCheckFunctionStatementResult<TypedFunctionStatementNode> visit(UntypedReturnNode node) {
                return typeCheckReturn(node, context);
            }

            @Override
            public TypeCheckFunctionStatementResult<TypedFunctionStatementNode> visit(UntypedSingleLineCommentNode node) {
                return typeCheckSingleLineCommentInFunction(node, context);
            }

            @Override
            public TypeCheckFunctionStatementResult<TypedFunctionStatementNode> visit(UntypedSwitchNode node) {
                return typeCheckSwitch(node, context);
            }

            @Override
            public TypeCheckFunctionStatementResult<TypedFunctionStatementNode> visit(UntypedVarNode node) {
                return typeCheckVar(node, context);
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
            typedStatements.add(statementResult.value());

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

        for (var untypedConditionalBranch : node.conditionalBranches()) {
            var result = typeCheckConditionalBranch(untypedConditionalBranch, context);
            typedConditionalBranches.add(result.value());
            returnBehaviours.add(result.returnBehaviour());
            returnType = Types.unify(returnType, result.returnType());
        }

        var typeCheckElseResult = typeCheckFunctionStatements(node.elseBody(), context);
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
        if (receiverType instanceof ListType listType) {
            expectExpressionType(typedIndexNode, Types.INT);
            return new TypedIndexNode(
                typedReceiverNode,
                typedIndexNode,
                listType.elementType(),
                node.source()
            );
        } else {
            throw new UnexpectedTypeError(IndexableTypeSet.INSTANCE, typedReceiverNode.type(), node.source());
        }
    }

    private static TypeCheckNamespaceStatementResult typeCheckInterface(UntypedInterfaceNode node) {
        var interfaceTypeBox = new Box<InterfaceType>();

        return new TypeCheckNamespaceStatementResult(
            List.of(
                new PendingTypeCheck(
                    TypeCheckerPhase.DEFINE_TYPES,
                    context -> {
                        // TODO: handle missing namespace name
                        var interfaceType = new InterfaceType(context.currentFrame().namespaceName().get(), node.name());
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

        var elementType = Types.unify(elementTypes);

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

        var typeCheckResults = new ArrayList<TypeCheckNamespaceStatementResult>();
        for (var statement : node.statements()) {
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

        var namespaceType = new NamespaceType(node.name(), fieldTypes);

        var typedNode = new TypedNamespaceNode(
            node.name(),
            typedImports,
            typedBody,
            namespaceType,
            node.sourceType(),
            node.source()
        );

        return new TypeCheckResult<>(typedNode, context.updateNamespaceType(namespaceType).leave());
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
        });
    }

    private static TypeCheckRecordBodyDeclarationResult typeCheckProperty(
        UntypedPropertyNode node,
        TypeCheckerContext initialContext
    ) {
        var typedTypeNode = typeCheckTypeLevelExpressionNode(node.type(), initialContext);
        var type = (Type) typedTypeNode.value();
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
                        // TODO: handle missing namespace name
                        var recordType = new RecordType(context.currentFrame().namespaceName().get(), node.name());
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
                            memberTypesBuilder.add(typedFieldNode.name(), (Type) typedFieldNode.type().value(), typedFieldNode.source());
                        }
                        for (var typeCheckDeclarationResult : typeCheckBodyDeclarationResults) {
                            memberTypesBuilder.addAll(typeCheckDeclarationResult.memberTypes(), typeCheckDeclarationResult.source());
                        }

                        var newContext = context.addFields(recordType, typedRecordFieldNodes);
                        var memberTypes = memberTypesBuilder.build();
                        memberTypesBox.set(memberTypes);
                        newContext = newContext.addMemberTypes(recordType, memberTypes);
                        for (var typedSupertypeNode : typedSupertypeNodes) {
                            // TODO: handle type-level values that aren't types
                            newContext = newContext.addSubtypeRelation(recordType, (InterfaceType) typedSupertypeNode.value());
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
        var expression = typeCheckExpression(node.expression(), context);

        if (context.returnType().isEmpty()) {
            throw new CannotReturnHereError(node.source());
        }
        var returnType = context.returnType().get();

        if (!context.isSubType(expression.type(), returnType)) {
            throw new UnexpectedTypeError(returnType, expression.type(), node.expression().source());
        }

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
        if (!(typedExpressionNode.type() instanceof InterfaceType)) {
            throw new UnexpectedTypeError(
                SealedInterfaceTypeSet.INSTANCE,
                typedExpressionNode.type(),
                typedExpressionNode.source()
            );
        }

        var returnBehaviours = new ArrayList<ReturnBehaviour>();
        var returnType = Types.NOTHING;

        var unhandledTypes = new HashSet<>(context.subtypesOf(typedExpressionNode.type()));

        var typedCaseNodes = new ArrayList<TypedSwitchCaseNode>();
        for (var switchCase : node.cases()) {
            var typedType = typeCheckTypeLevelExpressionNode(switchCase.type(), context);
            // TODO: handle not a type
            var caseType = (Type) typedType.value();
            var bodyContext = context.addLocal(switchCase.variableName(), caseType, switchCase.source());
            var typedBody = typeCheckFunctionStatements(switchCase.body(), bodyContext);
            var typedCaseNode = new TypedSwitchCaseNode(typedType, switchCase.variableName(), typedBody.value(), node.source());
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
}
