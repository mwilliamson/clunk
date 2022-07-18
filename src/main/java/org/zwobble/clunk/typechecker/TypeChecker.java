package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.ast.untyped.*;
import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.zwobble.clunk.types.Types.isSubType;
import static org.zwobble.clunk.types.Types.metaType;

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

    private static TypeCheckFunctionStatementResult<TypedFunctionStatementNode> typeCheckBlankLineInFunction(
        UntypedBlankLineNode node,
        TypeCheckerContext context
    ) {
        return new TypeCheckFunctionStatementResult<>(new TypedBlankLineNode(node.source()), false, context);
    }

    private static TypeCheckNamespaceStatementResult typeCheckBlankLineInNamespace(
        UntypedBlankLineNode node
    ) {
        return new TypeCheckNamespaceStatementResult(
            List.of(),
            context -> new TypedBlankLineNode(node.source())
        );
    }

    private static TypedExpressionNode typeCheckBoolLiteral(UntypedBoolLiteralNode node) {
        return new TypedBoolLiteralNode(node.value(), node.source());
    }

    private static TypedExpressionNode typeCheckCall(UntypedCallNode node, TypeCheckerContext context) {
        var receiver = typeCheckExpression(node.receiver(), context);

        // TODO: handle not callable
        List<Type> positionalParams;
        Type returnType;
        if (receiver.type() instanceof StaticFunctionType functionType) {
            positionalParams = functionType.positionalParams();
            returnType = functionType.returnType();
        } else if (receiver.type() instanceof TypeLevelValueType typeLevelValueType && typeLevelValueType.value() instanceof RecordType recordType) {
            positionalParams = context.fieldsOf(recordType).stream()
                .map(field -> (Type)field.type().value())
                .toList();
            returnType = recordType;
        } else {
            throw new UnsupportedOperationException("TODO");
        }

        if (node.positionalArgs().size() != positionalParams.size()) {
            throw new WrongNumberOfArgumentsError(
                positionalParams.size(),
                node.positionalArgs().size(),
                node.source()
            );
        }
        var typedPositionalArgs = node.positionalArgs().stream().map(arg -> typeCheckExpression(arg, context)).toList();

        for (var argIndex = 0; argIndex < positionalParams.size(); argIndex++) {
            var paramType = positionalParams.get(argIndex);
            var argNode = typedPositionalArgs.get(argIndex);
            var argType = argNode.type();
            if (!isSubType(argType, paramType)) {
                throw new UnexpectedTypeError(paramType, argType, argNode.source());
            }
        }
        return new TypedCallNode(
            receiver,
            typedPositionalArgs,
            returnType,
            node.source()
        );
    }

    private static TypeCheckFunctionStatementResult<TypedConditionalBranchNode> typeCheckConditionalBranch(
        UntypedConditionalBranchNode node,
        TypeCheckerContext context
    ) {
        var typedConditionNode = typeCheckExpression(node.condition(), context);

        if (!typedConditionNode.type().equals(Types.BOOL)) {
            throw new UnexpectedTypeError(Types.BOOL, typedConditionNode.type(), typedConditionNode.source());
        }

        var typeCheckBodyResults = typeCheckFunctionStatements(node.body(), context);
        var typedNode = new TypedConditionalBranchNode(
            typedConditionNode,
            typeCheckBodyResults.value(),
            node.source()
        );

        return new TypeCheckFunctionStatementResult<>(typedNode, typeCheckBodyResults.returns(), typeCheckBodyResults.context());
    }

    private static TypedTypeLevelExpressionNode typeCheckConstructedTypeNode(
        UntypedConstructedTypeNode node,
        TypeCheckerContext context
    ) {
        var typedReceiverNode = typeCheckTypeLevelExpressionNode(node.receiver(), context);
        var receiverType = typedReceiverNode.value();

        if (receiverType instanceof TypeConstructor typeConstructor) {
            var typedArgNodes = node.args().stream().map(arg -> typeCheckTypeLevelExpressionNode(arg, context)).toList();
            // TODO: handle non-type args
            var args = typedArgNodes.stream().map(arg -> (Type) arg.value()).toList();
            var constructedType = typeConstructor.call(args);

            return new TypedConstructedTypeNode(typedReceiverNode, typedArgNodes, constructedType, node.source());
        }

        throw new UnexpectedTypeError(
            TypeConstructorTypeSet.INSTANCE,
            // TODO: remove cast
            (Type) receiverType,
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
                        return context.updateType(node.name(), Types.metaType(type), node.source());
                    }
                )
            ),
            context -> new TypedEnumNode(typeBox.get(), node.source())
        );
    }

    public static TypedExpressionNode typeCheckExpression(
        UntypedExpressionNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedExpressionNode.Visitor<TypedExpressionNode>() {
            @Override
            public TypedExpressionNode visit(UntypedBoolLiteralNode node) {
                return typeCheckBoolLiteral(node);
            }

            @Override
            public TypedExpressionNode visit(UntypedCallNode node) {
                return typeCheckCall(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedFieldAccessNode node) {
                return typeCheckFieldAccess(node, context);
            }

            @Override
            public TypedExpressionNode visit(UntypedIntLiteralNode node) {
                return typeCheckIntLiteral(node);
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
        return new TypeCheckFunctionStatementResult<>(typedStatement, false, context);
    }

    private static TypedExpressionNode typeCheckFieldAccess(UntypedFieldAccessNode node, TypeCheckerContext context) {
        var typedReceiverNode = typeCheckExpression(node.receiver(), context);
        // TODO: handle not a record
        var recordType = (RecordType) typedReceiverNode.type();
        var field = context.fieldsOf(recordType)
            .stream()
            .filter(f -> f.name().equals(node.fieldName()))
            .findFirst();

        if (field.isEmpty()) {
            throw new UnknownFieldError(recordType, node.fieldName(), node.source());
        }

        var fieldType = (Type) field
            .get()
            .type()
            .value();

        return new TypedFieldAccessNode(
            typedReceiverNode,
            node.fieldName(),
            fieldType,
            node.source()
        );
    }

    private static TypeCheckNamespaceStatementResult typeCheckFunction(
        UntypedFunctionNode node
    ) {
        var functionTypeBox = new Box<StaticFunctionType>();
        var typedParamNodesBox = new Box<List<TypedParamNode>>();
        var typedReturnTypeNodeBox = new Box<TypedTypeLevelExpressionNode>();

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
                        return context.updateType(node.name(), type, node.source());
                    }
                )
            ),
            context -> {
                var functionType = functionTypeBox.get();
                var typedParamNodes = typedParamNodesBox.get();
                var typedReturnTypeNode = typedReturnTypeNodeBox.get();

                var typeCheckStatementsResult = typeCheckFunctionStatements(
                    node.body(),
                    context.enterFunction(functionType.returnType())
                );

                if (!typeCheckStatementsResult.returns() && !functionType.returnType().equals(Types.UNIT)) {
                    throw new MissingReturnError(node.source());
                }

                return new TypedFunctionNode(
                    node.name(),
                    typedParamNodes,
                    typedReturnTypeNode,
                    typeCheckStatementsResult.value(),
                    node.source()
                );
            }
        );
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
        var returns = false;

        for (var statement : body) {
            var statementResult = typeCheckFunctionStatement(statement, context);
            context = statementResult.context();
            typedStatements.add(statementResult.value());
            returns = returns || statementResult.returns();
        }

        return new TypeCheckFunctionStatementResult<>(typedStatements, returns, context);
    }

    private static TypeCheckFunctionStatementResult<TypedFunctionStatementNode> typeCheckIfStatement(
        UntypedIfStatementNode node,
        TypeCheckerContext context
    ) {
        var typedConditionalBranches = new ArrayList<TypedConditionalBranchNode>();
        var allBranchesReturn = true;

        for (var untypedConditionalBranch : node.conditionalBranches()) {
            var result = typeCheckConditionalBranch(untypedConditionalBranch, context);
            typedConditionalBranches.add(result.value());
            allBranchesReturn = allBranchesReturn && result.returns();
        }

        var typeCheckElseResult = typeCheckFunctionStatements(node.elseBody(), context);
        allBranchesReturn = allBranchesReturn && typeCheckElseResult.returns();

        var typedNode = new TypedIfStatementNode(
            typedConditionalBranches,
            typeCheckElseResult.value(),
            node.source()
        );

        return new TypeCheckFunctionStatementResult<>(typedNode, allBranchesReturn, context);
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

            var newContext = context.updateType(fieldName, importType, import_.source());

            var typedNode = new TypedImportNode(import_.namespaceName(), import_.fieldName(), importType, import_.source());

            return new TypeCheckImportResult(typedNode, newContext);
        } else {
            throw new RuntimeException("TODO");
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
                        return context.updateType(node.name(), metaType(interfaceType), node.source());
                    }
                )
            ),
            context ->  new TypedInterfaceNode(node.name(), interfaceTypeBox.get(), node.source())
        );
    }

    private static TypedExpressionNode typeCheckIntLiteral(UntypedIntLiteralNode node) {
        return new TypedIntLiteralNode(node.value(), node.source());
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
        for (var result : typeCheckResults) {
            typedBody.add(result.value(context));
        }

        var typedNode = new TypedNamespaceNode(
            node.name(),
            typedImports,
            typedBody,
            node.source()
        );
        return new TypeCheckResult<>(typedNode, context.leave());
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
            public TypeCheckNamespaceStatementResult visit(UntypedTestNode node) {
                return typeCheckTest(node);
            }
        });
    }

    private static TypeCheckNamespaceStatementResult typeCheckRecord(
        UntypedRecordNode node
    ) {
        var recordTypeBox = new Box<RecordType>();

        return new TypeCheckNamespaceStatementResult(
            List.of(
                new PendingTypeCheck(
                    TypeCheckerPhase.DEFINE_TYPES,
                    context -> {
                        // TODO: handle missing namespace name
                        var recordType = new RecordType(context.currentFrame().namespaceName().get(), node.name());
                        recordTypeBox.set(recordType);
                        return context.updateType(node.name(), metaType(recordType), node.source());
                    }
                ),
                new PendingTypeCheck(
                    TypeCheckerPhase.GENERATE_TYPE_INFO,
                    context -> {
                        var recordType = recordTypeBox.get();

                        var typedRecordFieldNodes = node.fields().stream()
                            .map(field -> typeCheckRecordField(field, context))
                            .collect(Collectors.toList());

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

                        var newContext = context.addFields(recordType, typedRecordFieldNodes);
                        for (var typedSupertypeNode : typedSupertypeNodes) {
                            // TODO: handle type-level values that aren't types
                            newContext = newContext.addSubtypeRelation(recordType, (InterfaceType) typedSupertypeNode.value());
                        }
                        return newContext;
                    }
                )
            ),
            context -> new TypedRecordNode(
                node.name(),
                recordTypeBox.get(),
                node.source()
            )
        );
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

    private static TypedExpressionNode typeCheckReference(UntypedReferenceNode node, TypeCheckerContext context) {
        var type = context.typeOf(node.name(), node.source());
        return new TypedReferenceNode(node.name(), type, node.source());
    }

    private static TypeCheckFunctionStatementResult<TypedFunctionStatementNode> typeCheckReturn(UntypedReturnNode node, TypeCheckerContext context) {
        var expression = typeCheckExpression(node.expression(), context);

        if (context.returnType().isEmpty()) {
            throw new CannotReturnHereError(node.source());
        }
        var returnType = context.returnType().get();

        if (!isSubType(expression.type(), returnType)) {
            throw new UnexpectedTypeError(returnType, expression.type(), node.expression().source());
        }

        var typedNode = new TypedReturnNode(expression, node.source());

        return new TypeCheckFunctionStatementResult<>(typedNode, true, context);
    }

    private static TypedExpressionNode typeCheckStringLiteral(UntypedStringLiteralNode node) {
        return new TypedStringLiteralNode(node.value(), node.source());
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
            }
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
        var updatedContext = context.updateType(node.name(), typedExpression.type(), node.source());
        return new TypeCheckFunctionStatementResult<>(typedNode, false, updatedContext);
    }

    private static TypeLevelValue resolveTypeLevelValue(String name, Source source, TypeCheckerContext context) {
        var type = context.typeOf(name, source);
        if (type instanceof TypeLevelValueType) {
            return ((TypeLevelValueType) type).value();
        } else {
            throw new RuntimeException("TODO");
        }
    }
}
