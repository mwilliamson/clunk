package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.ast.untyped.*;
import org.zwobble.clunk.errors.InternalCompilerError;
import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.*;
import org.zwobble.clunk.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.zwobble.clunk.types.Types.listElementType;
import static org.zwobble.clunk.types.Types.metaType;
import static org.zwobble.clunk.util.Iterables.slidingPairs;
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

    private record TypeCheckArgsResult(
        TypedArgsNode typedArgsNode,
        SignatureNonGeneric signatureNonGeneric
    ) {
    }

    private static TypeCheckArgsResult typeCheckArgs(
        Signature signature,
        Type expectedReturnType,
        UntypedCallNode node,
        TypeCheckerContext context
    ) {
        if (node.positionalArgs().size() != signature.positionalParamCount()) {
            throw new WrongNumberOfArgumentsError(
                signature.positionalParamCount(),
                node.positionalArgs().size(),
                node.source()
            );
        }

        var positionalChecks = new ArrayList<Pair<UntypedExpressionNode, Type>>();
        for (var argIndex = 0; argIndex < signature.positionalParamCount(); argIndex++) {
            var paramType = signature.positionalParam(argIndex);
            var untypedArgNode = node.positionalArgs().get(argIndex);
            positionalChecks.add(Pair.of(untypedArgNode, paramType));
        }

        var namedParams = signature.namedParams().stream()
            .collect(Collectors.toMap(param -> param.name(), param -> param));

        var namedChecks = new ArrayList<Pair<UntypedNamedArgNode, Type>>();

        for (var untypedNamedArgNode : node.namedArgs()) {
            var namedParam = namedParams.remove(untypedNamedArgNode.name());
            if (namedParam == null) {
                throw new ExtraNamedArgError(untypedNamedArgNode.name(), untypedNamedArgNode.source());
            }
            namedChecks.add(Pair.of(untypedNamedArgNode, namedParam.type()));
        }

        for (var missingParam : namedParams.values()) {
            throw new NamedArgIsMissingError(missingParam.name(), node.source());
        }

        if (signature instanceof SignatureGeneric signatureGeneric) {
            var typeConstraintSolver = new TypeConstraintSolver(
                signatureGeneric.typeParams(),
                context.subtypeRelations()
            );

            if (!typeConstraintSolver.addSubtypeConstraint(signatureGeneric.returnType(), expectedReturnType)) {
                throw new UnexpectedTypeError(
                    expectedReturnType,
                    signatureGeneric.returnType(),
                    node.source()
                );
            }

            for (var positionalCheck : positionalChecks) {
                var untypedExpressionNode = positionalCheck.first();
                var expressionType = typeCheckExpression(untypedExpressionNode, context).type();

                if (!typeConstraintSolver.addSubtypeConstraint(expressionType, positionalCheck.second())) {
                    throw new UnexpectedTypeError(
                        positionalCheck.second(),
                        expressionType,
                        untypedExpressionNode.source()
                    );
                }
            }
            for (var namedCheck : namedChecks) {
                var untypedExpressionNode = namedCheck.first().expression();
                var expressionType = typeCheckExpression(untypedExpressionNode, context).type();

                if (!typeConstraintSolver.addSubtypeConstraint(expressionType, namedCheck.second())) {
                    throw new UnexpectedTypeError(
                        namedCheck.second(),
                        expressionType,
                        untypedExpressionNode.source()
                    );
                }
            }
            var typeArgs = typeConstraintSolver.solve(node.source());
            return typeCheckArgs(
                signatureGeneric.typeArgs(typeArgs),
                expectedReturnType,
                node,
                context
            );
        } else if (signature instanceof SignatureNonGeneric signatureNonGeneric) {
            for (var namedArgPair : slidingPairs(node.args().named())) {
                if (namedArgPair.first().name().compareTo(namedArgPair.second().name()) > 0) {
                    throw new NamedArgsNotInLexicographicalOrderError(namedArgPair.second().source());
                }
            }

            var typedPositionalArgs = new ArrayList<TypedExpressionNode>();

            for (var positionalCheck : positionalChecks) {
                var untypedArgNode = positionalCheck.first();
                var paramType = positionalCheck.second();
                var typedArgNode = typeCheckExpression(
                    untypedArgNode,
                    paramType,
                    context
                );

                typedPositionalArgs.add(typedArgNode);
            }

            var typedNamedArgs = new ArrayList<TypedNamedArgNode>();

            for (var namedCheck : namedChecks) {
                var untypedNamedArgNode = namedCheck.first();
                var paramType = namedCheck.second();
                var typedExpression = typeCheckExpression(
                    untypedNamedArgNode.expression(),
                    paramType,
                    context
                );
                typedNamedArgs.add(new TypedNamedArgNode(
                    untypedNamedArgNode.name(),
                    typedExpression,
                    untypedNamedArgNode.source()
                ));
            }

            var typedArgsNode = new TypedArgsNode(typedPositionalArgs, typedNamedArgs, node.args().source());

            return new TypeCheckArgsResult(typedArgsNode, signatureNonGeneric);
        } else {
            // TODO: tidy up signature hierarchy to remove this
            throw new InternalCompilerError("signature should be generic or non-generic");
        }
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
            () -> new TypedBlankLineNode(node.source()),
            () -> Optional.empty()
        );
    }

    private static TypedExpressionNode typeCheckBoolLiteral(UntypedBoolLiteralNode node) {
        return new TypedBoolLiteralNode(node.value(), node.source());
    }

    private static TypedExpressionNode typeCheckCall(
        UntypedCallNode node,
        Type expected,
        TypeCheckerContext context
    ) {
        var receiver = typeCheckExpression(node.receiver(), context);
        var signature = Signatures.toSignature(receiver.type(), context, receiver.source());

        var typeCheckTypeArgsResult = typeCheckTypeArgs(signature, node, context);

        var typeCheckArgsResult = typeCheckArgs(
            typeCheckTypeArgsResult.signature(),
            expected,
            node,
            context
        );
        var typedArgs = typeCheckArgsResult.typedArgsNode();
        var signatureNonGeneric = typeCheckArgsResult.signatureNonGeneric();

        return switch (signatureNonGeneric.type()) {
            case ConstructorType ignored -> new TypedCallConstructorNode(
                receiver,
                typeCheckTypeArgsResult.nodes,
                typedArgs,
                signatureNonGeneric.returnType(),
                node.source()
            );
            case MethodType ignored -> {
                if (receiver instanceof TypedMemberAccessNode memberAccess) {
                    yield new TypedCallMethodNode(
                        Optional.of(memberAccess.receiver()),
                        memberAccess.memberName(),
                        typedArgs,
                        signatureNonGeneric.returnType(),
                        node.source()
                    );
                } else if (receiver instanceof TypedMemberReferenceNode memberReference) {
                    yield new TypedCallMethodNode(
                        Optional.empty(),
                        memberReference.name(),
                        typedArgs,
                        signatureNonGeneric.returnType(),
                        node.source()
                    );
                } else {
                    throw new InternalCompilerError("unexpected receiver for method");
                }

            }
            case StaticFunctionType staticFunctionType -> new TypedCallStaticFunctionNode(
                receiver,
                typedArgs,
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

    private record TypeCheckConditionResult(
        TypedExpressionNode typedNode,
        Optional<TypedTypeNarrowNode> typeNarrowNode
    ) {
    }

    private static TypeCheckConditionResult typeCheckCondition(
        UntypedExpressionNode node,
        TypeCheckerContext context
    ) {
        var typedConditionNode = typeCheckExpression(node, context);
        expectExpressionType(typedConditionNode, Types.BOOL);

        if (
            typedConditionNode instanceof TypedInstanceOfNode typedInstanceOfNode &&
            typedInstanceOfNode.expression() instanceof TypedReferenceNode typedInstanceOfReferenceNode
        ) {
            var narrowedType = (StructuredType) typedInstanceOfNode.typeExpression().value();
            var variableName = typedInstanceOfReferenceNode.name();

            return new TypeCheckConditionResult(
                typedConditionNode,
                Optional.of(new TypedTypeNarrowNode(variableName, narrowedType, typedInstanceOfNode.source()))
            );
        } else {
            return new TypeCheckConditionResult(typedConditionNode, Optional.empty());
        }
    }

    private static TypeCheckFunctionStatementResult<TypedConditionalBranchNode> typeCheckConditionalBranch(
        UntypedConditionalBranchNode node,
        TypeCheckerContext context
    ) {
        var conditionTypeCheckResult = typeCheckCondition(node.condition(), context);

        var bodyContext = context;
        var bodyPrefix = new ArrayList<TypedFunctionStatementNode>();
        if (conditionTypeCheckResult.typeNarrowNode.isPresent()) {
            var typeNarrowNode = conditionTypeCheckResult.typeNarrowNode.get();
            bodyContext = bodyContext.updateLocal(
                typeNarrowNode.variableName(),
                typeNarrowNode.type(),
                typeNarrowNode.source()
            );
            bodyPrefix.add(typeNarrowNode);
        }

        var typeCheckBodyResults = typeCheckFunctionStatements(node.body(), bodyContext);

        return typeCheckBodyResults.map(body -> new TypedConditionalBranchNode(
            conditionTypeCheckResult.typedNode,
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
                        var type = new EnumType(context.namespaceId(), node.name(), node.members());
                        typeBox.set(type);
                        return context.addLocal(node.name(), Types.metaType(type), node.source());
                    }
                )
            ),
            () -> new TypedEnumNode(typeBox.get(), node.source()),
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

        // TODO: generalise structured equality
        if (left.type().equals(Types.map(Types.STRING, Types.STRING))) {
            expectExpressionType(right, Types.map(Types.STRING, Types.STRING));
            return new TypedStructuredEqualsNode(left, right, node.source());
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
        var typed = node.accept(new UntypedExpressionNode.Visitor<TypedExpressionNode>() {
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
                return typeCheckCall(node, expected, context);
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
            public TypedExpressionNode visit(UntypedListComprehensionNode node) {
                return typeCheckListComprehension(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedListLiteralNode node) {
                return typeCheckListLiteral(node, expected, context);
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
            public TypedExpressionNode visit(UntypedMapLiteralNode node) {
                return typeCheckMapLiteral(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedMemberAccessNode node) {
                return typeCheckMemberAccess(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedMemberDefinitionReferenceNode node) {
                return typeCheckMemberDefinitionReference(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedNotEqualNode node) {
                return typeCheckNotEqual(node, context);
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

        if (expected instanceof FunctionValueType && typed.type() instanceof StaticFunctionType staticFunctionType) {
            var functionType = new FunctionValueType(
                staticFunctionType.params(),
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
        return typeCheckExpression(node, Types.OBJECT, context);
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
        var typeCheckIterableResult = typeCheckIterableExpression(node.iterable(), context);
        var typedIterable = typeCheckIterableResult.iterable();
        var targetType = typeCheckIterableResult.elementType();

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

    private static class FunctionTypeChecker {
        private final Box<TypedFunctionSignatureNode> typedSignatureBox = new Box<>();
        private final Box<List<TypedFunctionStatementNode>> typedBodyBox = new Box<>();
        private final UntypedFunctionNode node;

        public FunctionTypeChecker(UntypedFunctionNode node) {
            this.node = node;
        }

        public void defineFunctionType(TypeCheckerContext context) {
            var signature = new UntypedFunctionSignatureNode(
                node.name(),
                node.params(),
                node.returnType(),
                node.source()
            );
            typedSignatureBox.set(typeCheckFunctionSignature(signature, context));
        }

        public void typeCheckBody(TypeCheckerContext context) {
            var signature = typedSignatureBox.get().signature();
            var typedParamsNode = typedSignatureBox.get().params();

            var bodyContext = context.enterFunction(signature.returnType());
            for (var typedParamNode : typedParamsNode.all()) {
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
        }

        public FunctionSignature signature() {
            return typedSignatureBox.get().signature();
        }

        public TypedFunctionNode typedNode() {
            var typedSignatureNode = typedSignatureBox.get();
            return new TypedFunctionNode(
                typedSignatureNode.name(),
                typedSignatureNode.params(),
                typedSignatureNode.returnType(),
                typedBodyBox.get(),
                typedSignatureNode.source()
            );
        }
    }

    private static TypeCheckNamespaceStatementResult typeCheckFunction(
        UntypedFunctionNode node
    ) {
        var functionTypeChecker = new FunctionTypeChecker(node);
        var typeBox = new Box<StaticFunctionType>();

        return new TypeCheckNamespaceStatementResult(
            List.of(
                new PendingTypeCheck(
                    TypeCheckerPhase.DEFINE_FUNCTIONS,
                    context -> {
                        functionTypeChecker.defineFunctionType(context);
                        var functionType = functionTypeChecker.signature();
                        var type = new StaticFunctionType(
                            context.namespaceId(),
                            node.name(),
                            Optional.empty(),
                            functionType.params(),
                            functionType.returnType(),
                            Visibility.PUBLIC
                        );
                        typeBox.set(type);
                        return context.addLocal(node.name(), type, node.source());
                    }
                ),

                new PendingTypeCheck(
                    TypeCheckerPhase.TYPE_CHECK_BODIES,
                    context -> {
                        functionTypeChecker.typeCheckBody(context);
                        return context;
                    }
                )
            ),
            () -> functionTypeChecker.typedNode(),
            () -> Optional.of(Map.entry(
                node.name(),
                typeBox.get()
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

    private static TypedFunctionSignatureNode typeCheckFunctionSignature(
        UntypedFunctionSignatureNode node,
        TypeCheckerContext context
    ) {
        var typedPositionalParamNodes = node.params().positional().stream()
            .map(param -> typeCheckParam(param, context))
            .toList();
        var positionalParamTypes = typedPositionalParamNodes.stream()
            .map(param -> typedTypeLevelExpressionToType(param.type()))
            .toList();

        var typedNamedParamNodes = node.params().named().stream()
            .map(param -> typeCheckParam(param, context))
            .toList();
        var namedParamTypes = typedNamedParamNodes.stream()
            .map(param -> Types.namedParam(param.name(), typedTypeLevelExpressionToType(param.type())))
            .toList();
        for (var namedParamPair : slidingPairs(node.params().named())) {
            if (namedParamPair.first().name().compareTo(namedParamPair.second().name()) > 0) {
                throw new NamedParamsNotInLexicographicalOrderError(namedParamPair.second().source());
            }
        }

        var typedParamsNode = new TypedParamsNode(
            typedPositionalParamNodes,
            typedNamedParamNodes,
            node.params().source()
        );
        var paramTypes = new ParamTypes(positionalParamTypes, namedParamTypes);

        var typedReturnTypeNode = typeCheckTypeLevelExpressionNode(node.returnType(), context);
        var returnType = typedTypeLevelExpressionToType(typedReturnTypeNode);

        var signature = new FunctionSignature(paramTypes, returnType);

        return new TypedFunctionSignatureNode(
            node.name(),
            typedParamsNode,
            typedReturnTypeNode,
            signature,
            node.source()
        );
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
                return typeCheckIfStatement(node, context);
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
            returnType = Types.commonSupertype(returnType, statementResult.returnType());
        }

        return new TypeCheckFunctionStatementResult<>(typedStatements, returnBehaviour, returnType, context);
    }

    private static TypeCheckFunctionStatementResult<List<TypedFunctionStatementNode>> typeCheckIfStatement(
        UntypedIfStatementNode node,
        TypeCheckerContext context
    ) {
        var typedConditionalBranches = new ArrayList<TypedConditionalBranchNode>();
        var returnBehaviours = new ArrayList<ReturnBehaviour>();
        var returnType = Types.NOTHING;

        var bodyContext = context;
        var bodyPrefix = new ArrayList<TypedFunctionStatementNode>();
        var typedStatements = new ArrayList<TypedFunctionStatementNode>();

        for (var untypedConditionalBranch : node.conditionalBranches()) {
            var result = typeCheckConditionalBranch(untypedConditionalBranch, bodyContext)
                .map(typedConditionalBranch ->
                    typedConditionalBranch.body().isEmpty()
                        ? typedConditionalBranch
                        : typedConditionalBranch.withBody(
                            Stream.concat(bodyPrefix.stream(), typedConditionalBranch.body().stream()).toList()
                        )
                );
            typedConditionalBranches.add(result.value());
            returnBehaviours.add(result.returnBehaviour());
            returnType = Types.commonSupertype(returnType, result.returnType());

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
                    typedStatements.add(new TypedTypeNarrowNode(variableName, narrowedType, typedConditionNotNode.source()));
                }
            }
        }

        var typeCheckElseResult = typeCheckFunctionStatements(node.elseBody(), bodyContext)
            .map(typedElseBody ->
                typedElseBody.isEmpty()
                    ? typedElseBody
                    : Stream.concat(bodyPrefix.stream(), typedElseBody.stream()).toList()
            );

        returnBehaviours.add(typeCheckElseResult.returnBehaviour());
        returnType = Types.commonSupertype(returnType, typeCheckElseResult.returnType());

        var typedNode = new TypedIfStatementNode(
            typedConditionalBranches,
            typeCheckElseResult.value(),
            node.source()
        );
        typedStatements.add(0, typedNode);

        return new TypeCheckFunctionStatementResult<>(
            typedStatements,
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

        var type = context.typeOfNamespace(import_.namespaceId());
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
        var typedBodyDeclarationsBox = new Box<List<? extends TypedInterfaceBodyDeclarationNode>>();

        return new TypeCheckNamespaceStatementResult(
            List.of(
                new PendingTypeCheck(
                    TypeCheckerPhase.DEFINE_TYPES,
                    context -> {
                        var interfaceType = new InterfaceType(context.namespaceId(), node.name(), node.isSealed());
                        interfaceTypeBox.set(interfaceType);

                        return context.addLocal(node.name(), metaType(interfaceType), node.source());
                    }
                ),

                new PendingTypeCheck(
                    TypeCheckerPhase.GENERATE_TYPE_INFO,
                    context -> {
                        var interfaceType = interfaceTypeBox.get();

                        var typeCheckBodyDeclarationResults = node.body().stream()
                            .map(declaration -> typeCheckInterfaceBodyDeclaration(declaration, context))
                            .toList();
                        var typedBodyDeclarations = typeCheckBodyDeclarationResults.stream()
                            .map(result -> result.value())
                            .toList();
                        typedBodyDeclarationsBox.set(typedBodyDeclarations);

                        var memberTypesBuilder = new MemberTypesBuilder();
                        for (var typeCheckDeclarationResult : typeCheckBodyDeclarationResults) {
                            memberTypesBuilder.addAll(typeCheckDeclarationResult.memberTypes(), typeCheckDeclarationResult.value().source());
                        }

                        var memberTypes = memberTypesBuilder.build();
                        return context.addMemberTypes(interfaceType, memberTypes);
                    }
                )
            ),
            () -> new TypedInterfaceNode(
                node.name(),
                interfaceTypeBox.get(),
                typedBodyDeclarationsBox.get(),
                node.source()
            ),
            () -> Optional.of(Map.entry(
                node.name(),
                Types.metaType(interfaceTypeBox.get())
            ))
        );
    }

    private static TypeCheckInterfaceBodyDeclarationResult typeCheckInterfaceBodyDeclaration(
        UntypedInterfaceBodyDeclarationNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedInterfaceBodyDeclarationNode.Visitor<>() {
            @Override
            public TypeCheckInterfaceBodyDeclarationResult visit(UntypedFunctionSignatureNode node) {
                var typedSignatureNode = typeCheckFunctionSignature(node, context);
                var methodType = functionSignatureToMethodType(typedSignatureNode.signature(), context);
                return new TypeCheckInterfaceBodyDeclarationResult(
                    Map.ofEntries(Map.entry(node.name(), methodType)),
                    typedSignatureNode
                );
            }
        });
    }

    private static TypedExpressionNode typeCheckIntLiteral(UntypedIntLiteralNode node) {
        return new TypedIntLiteralNode(node.value(), node.source());
    }

    record TypeCheckIterableResult(TypedExpressionNode iterable, Type elementType) {}

    static TypeCheckIterableResult typeCheckIterableExpression(
        UntypedExpressionNode expression,
        TypeCheckerContext context
    ) {
        var typedIterable = typeCheckExpression(expression, Types.list(Types.OBJECT), context);
        var iterableType = typedIterable.type();

        if (!(iterableType instanceof ConstructedType iterableTypeList)) {
            throw new UnexpectedTypeError(
                Types.LIST_CONSTRUCTOR.genericType(),
                iterableType,
                expression.source()
            );
        }

        // TODO: check this is a list

        var elementType = iterableTypeList.args().get(0);

        return new TypeCheckIterableResult(typedIterable, elementType);
    }

    private static TypedExpressionNode typeCheckListComprehension(
        UntypedListComprehensionNode node,
        TypeCheckerContext context
    ) {
        var typedForClauses = new ArrayList<TypedComprehensionForClauseNode>();
        var comprehensionContext = context.enterComprehension();

        for (var forClause : node.forClauses()) {
            var typeCheckIterableResult = typeCheckIterableExpression(forClause.iterable(), comprehensionContext);

            comprehensionContext = comprehensionContext.addLocal(
                forClause.targetName(),
                typeCheckIterableResult.elementType(),
                forClause.source()
            );

            var typedIfClauses = new ArrayList<TypedComprehensionIfClauseNode>();

            for (var condition : forClause.conditions()) {
                var result = typeCheckCondition(condition, comprehensionContext);
                var typeNarrow = result.typeNarrowNode()
                    .filter(x -> x.variableName().equals(forClause.targetName()));

                if (typeNarrow.isPresent()) {
                    comprehensionContext = comprehensionContext.updateLocal(
                        typeNarrow.get().variableName(),
                        typeNarrow.get().type(),
                        typeNarrow.get().source()
                    );
                }

                var typedIfClause = new TypedComprehensionIfClauseNode(
                    result.typedNode(),
                    typeNarrow.map(x -> x.type()),
                    result.typedNode().source()
                );
                typedIfClauses.add(typedIfClause);
            }

            typedForClauses.add(new TypedComprehensionForClauseNode(
                forClause.targetName(),
                typeCheckIterableResult.elementType(),
                typeCheckIterableResult.iterable(),
                typedIfClauses,
                forClause.source()
            ));
        }

        var typedYield = typeCheckExpression(node.yield(), comprehensionContext);

        return new TypedListComprehensionNode(
            typedForClauses,
            typedYield,
            node.source()
        );
    }

    private static TypedExpressionNode typeCheckListLiteral(
        UntypedListLiteralNode node,
        Type expectedType,
        TypeCheckerContext context
    ) {
        var expectedElementType = listElementType(expectedType);

        var elements = node.elements().stream()
            .map(element -> typeCheckExpression(element, expectedElementType.orElse(Types.OBJECT), context))
            .toList();

        var elementTypes = elements.stream()
            .map(element -> element.type())
            .distinct()
            .toList();

        var elementType = expectedElementType.orElseGet(() -> Types.commonSupertype(elementTypes, Types.NOTHING));

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

    private static TypedExpressionNode typeCheckMapLiteral(
        UntypedMapLiteralNode node,
        TypeCheckerContext context
    ) {
        var entries = node.entries().stream()
            .map(entry -> new TypedMapEntryLiteralNode(
                typeCheckExpression(entry.key(), context),
                typeCheckExpression(entry.value(), context),
                entry.source()
            ))
            .toList();

        var keyTypes = entries.stream()
            .map(entry -> entry.keyType())
            .distinct()
            .toList();
        var keyType = Types.commonSupertype(keyTypes, Types.NOTHING);

        var valueTypes = entries.stream()
            .map(entry -> entry.valueType())
            .distinct()
            .toList();
        var valueType = Types.commonSupertype(valueTypes, Types.NOTHING);

        return new TypedMapLiteralNode(
            entries,
            keyType,
            valueType,
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

    private static TypedExpressionNode typeCheckMemberDefinitionReference(
        UntypedMemberDefinitionReferenceNode node,
        TypeCheckerContext context
    ) {
        var receiver = typeCheckExpression(node.receiver(), context);
        // TODO: exception when receiver is not meta-type
        var receiverType = (Type) ((TypeLevelValueType) receiver.type()).value();
        var memberType = context.memberType(receiverType, node.memberName());

        if (memberType.isEmpty()) {
            throw new UnknownMemberError(receiverType, node.memberName(), node.operatorSource());
        }

        return new TypedMemberDefinitionReferenceNode(
            receiver,
            node.memberName(),
            Types.member(receiverType, memberType.get()),
            node.operatorSource(),
            node.source()
        );
    }

    private static TypeCheckRecordBodyDeclarationResult typeCheckMethod(
        UntypedFunctionNode node,
        TypeCheckerContext context
    ) {
        var functionTypeChecker = new FunctionTypeChecker(node);
        functionTypeChecker.defineFunctionType(context);

        var functionType = functionTypeChecker.signature();
        var type = functionSignatureToMethodType(functionType, context);

        return new TypeCheckRecordBodyDeclarationResult(
            Map.ofEntries(Map.entry(node.name(), type)),
            bodyContext -> {
                functionTypeChecker.typeCheckBody(bodyContext);
                return functionTypeChecker.typedNode();
            },
            node.source()
        );
    }

    private static MethodType functionSignatureToMethodType(FunctionSignature signature, TypeCheckerContext context) {
        return new MethodType(
            context.namespaceId(),
            Optional.empty(),
            signature.params(),
            signature.returnType(),
            Visibility.PUBLIC
        );
    }

    public static TypeCheckResult<TypedNamespaceNode> typeCheckNamespace(
        UntypedNamespaceNode node,
        TypeCheckerContext context
    ) {
        context = context.enterNamespace(node.id());

        var typedImports = new ArrayList<TypedImportNode>();
        for (var import_ : node.imports()) {
            var typeCheckImportResult = typeCheckImport(import_, context);
            context = typeCheckImportResult.context;
            typedImports.add(typeCheckImportResult.node);
        }

        var typeCheckBodyResult = typeCheckNamespaceStatements(node.statements(), context);
        context = typeCheckBodyResult.context;

        var namespaceType = new NamespaceType(node.id(), typeCheckBodyResult.fieldTypes);

        var typedNode = new TypedNamespaceNode(
            node.id(),
            typedImports,
            typeCheckBodyResult.typedBody,
            namespaceType,
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
                context = typeCheckResult.typeCheckPhase(phase, context);
            }
        }

        var typedBody = new ArrayList<TypedNamespaceStatementNode>();
        var fieldTypes = new HashMap<String, Type>();
        for (var result : typeCheckResults) {
            typedBody.add(result.value());
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

    private static TypedExpressionNode typeCheckNotEqual(
        UntypedNotEqualNode node,
        TypeCheckerContext context
    ) {
        var left = typeCheckExpression(node.left(), context);
        var right = typeCheckExpression(node.right(), context);

        if (left.type().equals(Types.INT)) {
            expectExpressionType(right, Types.INT);
            return new TypedIntNotEqualNode(left, right, node.source());
        }

        // TODO: don't assume strings if not ints
        expectExpressionType(left, Types.STRING);
        expectExpressionType(right, Types.STRING);

        return new TypedStringNotEqualNode(
            left,
            right,
            node.source()
        );
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
                        var recordType = new RecordType(context.namespaceId(), node.name());
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
                                    if (!supertype.namespaceId().equals(recordType.namespaceId()) && supertype.isSealed()) {
                                        throw new CannotExtendSealedInterfaceFromDifferentNamespaceError(untypedSupertypeNode.source());
                                    }
                                } else {
                                    throw new CannotExtendFinalTypeError(untypedSupertypeNode.source());
                                }
                                return typedSupertypeNode;
                            })
                            .toList();
                        // TODO: check compatibility with supertypes
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

                        var constructorPositionalParams = typedRecordFieldNodes.stream()
                            .map(fieldNode -> typedTypeLevelExpressionToType(fieldNode.type()))
                            .toList();
                        var constructorParams = new ParamTypes(constructorPositionalParams, List.of());
                        var newContext = context.addConstructorType(new ConstructorType(
                            recordType.namespaceId(),
                            Optional.empty(),
                            constructorParams,
                            recordType,
                            Visibility.PUBLIC
                        ));
                        var memberTypes = memberTypesBuilder.build();
                        memberTypesBox.set(memberTypes);
                        newContext = newContext.addMemberTypes(recordType, memberTypes);
                        for (var typedSupertypeNode : typedSupertypeNodes) {
                            // TODO: handle type-level values that aren't types
                            var interfaceType = (InterfaceType) typedSupertypeNode.value();
                            newContext = newContext.addSubtypeRelation(recordType, interfaceType);
                            if (interfaceType.isSealed()) {
                                newContext = newContext.addSealedInterfaceCase(interfaceType, recordType);
                            }
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
            () -> new TypedRecordNode(
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
            public TypeCheckRecordBodyDeclarationResult visit(UntypedFunctionNode node) {
                return typeCheckMethod(node, context);
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
            () -> typeCheckSingleLineComment(node),
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
            returnType = Types.commonSupertype(returnType, typedBody.returnType());

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
        var typedStatementsBox = new Box<List<TypedFunctionStatementNode>>();

        return new TypeCheckNamespaceStatementResult(
            List.of(
                new PendingTypeCheck(
                    TypeCheckerPhase.TYPE_CHECK_BODIES,
                    context -> {
                        var typedStatements = typeCheckFunctionStatements(
                            node.body(),
                            context.enterTest()
                        ).value();

                        typedStatementsBox.set(typedStatements);

                        return context;
                    }
                )
            ),
            () -> new TypedTestNode(
                node.name(),
                typedStatementsBox.get(),
                node.source()
            ),
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
            () -> new TypedTestSuiteNode(
                node.name(),
                typeCheckBodyResultBox.get().typedBody,
                node.source()
            ),
            () -> Optional.empty()
        );
    }

    private record TypeCheckTypeArgsResult(
        Optional<List<TypedTypeLevelExpressionNode>> nodes,
        Signature signature
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
                    return new TypeCheckTypeArgsResult(Optional.empty(), signatureGeneric);
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

                    var nonGenericSignature = signatureGeneric.typeArgs(
                        typedArgs.stream()
                            .map(arg -> (Type) arg.value())
                            .toList()
                    );

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

    private static StructuredType verifyIsSealedInterfaceType(TypedExpressionNode typedExpressionNode) {
        var type = Types.toSealedInterfaceType(typedExpressionNode.type());
        return type.orElseThrow(() -> new UnexpectedTypeError(
            SealedInterfaceTypeSet.INSTANCE,
            typedExpressionNode.type(),
            typedExpressionNode.source()
        ));
    }
}
