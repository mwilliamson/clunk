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

    private static TypeCheckerContext defineVariablesForBlankLine(UntypedBlankLineNode node, TypeCheckerContext context) {
        return context;
    }

    private static TypeCheckStatementResult<TypedFunctionStatementNode> typeCheckBlankLineInFunction(
        UntypedBlankLineNode node,
        TypeCheckerContext context
    ) {
        return new TypeCheckStatementResult<>(new TypedBlankLineNode(node.source()), false, context);
    }

    private static TypeCheckResult<TypedNamespaceStatementNode> typeCheckBlankLineInNamespace(
        UntypedBlankLineNode node,
        TypeCheckerContext context
    ) {
        return new TypeCheckResult<>(new TypedBlankLineNode(node.source()), context);
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

    private static TypeCheckStatementResult<TypedConditionalBranchNode> typeCheckConditionalBranch(
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

        return new TypeCheckStatementResult<>(typedNode, typeCheckBodyResults.returns(), typeCheckBodyResults.context());
    }

    private static TypedTypeLevelExpressionNode typeCheckConstructedTypeNode(
        UntypedConstructedTypeNode node,
        TypeCheckerContext context
    ) {
        var receiverType = typeCheckTypeLevelExpressionNode(node.receiver(), context).value();

        if (receiverType instanceof TypeConstructor typeConstructor) {
            // TODO: handle non-type args
            var args = node.args().stream().map(arg -> (Type)typeCheckTypeLevelExpressionNode(arg, context).value()).toList();
            var constructedType = typeConstructor.call(args);

            return new TypedTypeLevelExpressionNode(constructedType, node.source());
        }

        throw new UnexpectedTypeError(
            TypeConstructorTypeSet.INSTANCE,
            // TODO: remove cast
            (Type) receiverType,
            node.receiver().source()
        );
    }

    private static TypeCheckerContext defineVariablesForEnum(
        UntypedEnumNode node,
        TypeCheckerContext context
    ) {
        var type = new EnumType(context.currentFrame().namespaceName().get(), node.name(), node.members());
        return context.updateType(node.name(), Types.metaType(type), node.source());
    }

    private static TypeCheckResult<TypedNamespaceStatementNode> typeCheckEnum(
        UntypedEnumNode node,
        TypeCheckerContext context
    ) {
        var type = (EnumType) resolveTypeLevelValue(node.name(), node.source(), context);
        var typedNode = new TypedEnumNode(type, node.source());
        return new TypeCheckResult<>(typedNode, context);
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

    private static TypeCheckStatementResult<TypedFunctionStatementNode> typeCheckExpressionStatement(
        UntypedExpressionStatementNode node,
        TypeCheckerContext context
    ) {
        var typedExpression = typeCheckExpression(node.expression(), context);
        var typedStatement = new TypedExpressionStatementNode(typedExpression, node.source());
        return new TypeCheckStatementResult<>(typedStatement, false, context);
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

    private static TypeCheckerContext defineVariablesForFunction(
        UntypedFunctionNode node,
        TypeCheckerContext context
    ) {
        // TODO: handle not a type
        var params = node.params().stream().map(param -> (Type) typeCheckParam(param, context).type().value()).toList();
        var typedReturnTypeNode = typeCheckTypeLevelExpressionNode(node.returnType(), context);
        // TODO: handle not a type
        var returnType = (Type) typedReturnTypeNode.value();

        var type = new StaticFunctionType(
            context.currentFrame().namespaceName().get(),
            node.name(),
            params,
            returnType
        );
        return context.updateType(node.name(), type, node.source());
    }

    private static TypeCheckResult<TypedNamespaceStatementNode> typeCheckFunction(
        UntypedFunctionNode node,
        TypeCheckerContext context
    ) {
        var functionType = (StaticFunctionType) context.typeOf(node.name(), node.source());

        var typedParamNodes = node.params().stream().map(param -> typeCheckParam(param, context)).toList();
        var typedReturnTypeNode = typeCheckTypeLevelExpressionNode(node.returnType(), context);

        var typeCheckStatementsResult = typeCheckFunctionStatements(
            node.body(),
            context.enterFunction(functionType.returnType())
        );

        if (!typeCheckStatementsResult.returns() && !functionType.returnType().equals(Types.UNIT)) {
            throw new MissingReturnError(node.source());
        }

        var typedNode = new TypedFunctionNode(
            node.name(),
            typedParamNodes,
            typedReturnTypeNode,
            typeCheckStatementsResult.value(),
            node.source()
        );

        return new TypeCheckResult<>(typedNode, context);
    }

    public static TypeCheckStatementResult<TypedFunctionStatementNode> typeCheckFunctionStatement(
        UntypedFunctionStatementNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedFunctionStatementNode.Visitor<>() {
            @Override
            public TypeCheckStatementResult<TypedFunctionStatementNode> visit(UntypedBlankLineNode node) {
                return typeCheckBlankLineInFunction(node, context);
            }

            @Override
            public TypeCheckStatementResult<TypedFunctionStatementNode> visit(UntypedExpressionStatementNode node) {
                return typeCheckExpressionStatement(node, context);
            }

            @Override
            public TypeCheckStatementResult<TypedFunctionStatementNode> visit(UntypedIfStatementNode node) {
                return typeCheckIfStatement(node, context);
            }

            @Override
            public TypeCheckStatementResult<TypedFunctionStatementNode> visit(UntypedReturnNode node) {
                return typeCheckReturn(node, context);
            }

            @Override
            public TypeCheckStatementResult<TypedFunctionStatementNode> visit(UntypedVarNode node) {
                return typeCheckVar(node, context);
            }
        });
    }

    private static TypeCheckStatementResult<List<TypedFunctionStatementNode>> typeCheckFunctionStatements(
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

        return new TypeCheckStatementResult<>(typedStatements, returns, context);
    }

    private static TypeCheckStatementResult<TypedFunctionStatementNode> typeCheckIfStatement(
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

        return new TypeCheckStatementResult<>(typedNode, allBranchesReturn, context);
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

    private static TypeCheckerContext defineVariablesForInterface(UntypedInterfaceNode node, TypeCheckerContext context) {
        // TODO: handle missing namespace name
        var interfaceType = new InterfaceType(context.currentFrame().namespaceName().get(), node.name());
        return context.updateType(node.name(), metaType(interfaceType), node.source());
    }

    private static TypeCheckResult<TypedNamespaceStatementNode> typeCheckInterface(UntypedInterfaceNode node, TypeCheckerContext context) {
        var interfaceType = (InterfaceType) resolveTypeLevelValue(node.name(), node.source(), context);
        var typedNode = new TypedInterfaceNode(node.name(), interfaceType, node.source());
        return new TypeCheckResult<>(typedNode, context);
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

        for (var statement : node.statements()) {
            if (statement.isTypeDefinition()) {
                context = defineVariablesForNamespaceStatement(statement, context);
            }
        }

        for (var statement : node.statements()) {
            if (!statement.isTypeDefinition()) {
                context = defineVariablesForNamespaceStatement(statement, context);
            }
        }

        var typedBody = new ArrayList<TypedNamespaceStatementNode>();
        for (var statement : node.statements()) {
            var result = typeCheckNamespaceStatement(statement, context);
            context = result.context();
            typedBody.add(result.typedNode());
        }

        var typedNode = new TypedNamespaceNode(
            node.name(),
            typedImports,
            typedBody,
            node.source()
        );
        return new TypeCheckResult<>(typedNode, context.leave());
    }

    public static TypeCheckerContext defineVariablesForNamespaceStatement(
        UntypedNamespaceStatementNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedNamespaceStatementNode.Visitor<TypeCheckerContext>() {
            @Override
            public TypeCheckerContext visit(UntypedBlankLineNode node) {
                return defineVariablesForBlankLine(node, context);
            }

            @Override
            public TypeCheckerContext visit(UntypedEnumNode node) {
                return defineVariablesForEnum(node, context);
            }

            @Override
            public TypeCheckerContext visit(UntypedFunctionNode node) {
                return defineVariablesForFunction(node, context);
            }

            @Override
            public TypeCheckerContext visit(UntypedInterfaceNode node) {
                return defineVariablesForInterface(node, context);
            }

            @Override
            public TypeCheckerContext visit(UntypedRecordNode node) {
                return defineVariablesForRecord(node, context);
            }

            @Override
            public TypeCheckerContext visit(UntypedTestNode node) {
                return defineVariablesForTest(node, context);
            }
        });
    }

    public static TypeCheckResult<TypedNamespaceStatementNode> typeCheckNamespaceStatement(
        UntypedNamespaceStatementNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedNamespaceStatementNode.Visitor<TypeCheckResult<TypedNamespaceStatementNode>>() {
            @Override
            public TypeCheckResult<TypedNamespaceStatementNode> visit(UntypedBlankLineNode node) {
                return typeCheckBlankLineInNamespace(node, context);
            }

            @Override
            public TypeCheckResult<TypedNamespaceStatementNode> visit(UntypedEnumNode node) {
                return typeCheckEnum(node, context);
            }

            @Override
            public TypeCheckResult<TypedNamespaceStatementNode> visit(UntypedFunctionNode node) {
                return typeCheckFunction(node, context);
            }

            @Override
            public TypeCheckResult<TypedNamespaceStatementNode> visit(UntypedInterfaceNode node) {
                return typeCheckInterface(node, context);
            }

            @Override
            public TypeCheckResult<TypedNamespaceStatementNode> visit(UntypedRecordNode node) {
                return typeCheckRecord(node, context);
            }

            @Override
            public TypeCheckResult<TypedNamespaceStatementNode> visit(UntypedTestNode node) {
                return typeCheckTest(node, context);
            }
        });
    }

    private static TypeCheckerContext defineVariablesForRecord(
        UntypedRecordNode node,
        TypeCheckerContext context
    ) {
        var recordType = new RecordType(context.currentFrame().namespaceName().get(), node.name());
        return context.updateType(node.name(), metaType(recordType), node.source());
    }

    private static TypeCheckResult<TypedNamespaceStatementNode> typeCheckRecord(
        UntypedRecordNode node,
        TypeCheckerContext context
    ) {
        var recordType = (RecordType) resolveTypeLevelValue(node.name(), node.source(), context);

        var typedNode = new TypedRecordNode(
            node.name(),
            recordType,
            node.source()
        );

        var newContext = context.addFields(recordType, node.fields().stream()
            .map(field -> typeCheckRecordField(field, context))
            .collect(Collectors.toList()));

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

        // TODO: handle missing namespace name


        for (var typedSupertypeNode : typedSupertypeNodes) {
            // TODO: handle type-level values that aren't types
            newContext = newContext.addSubtypeRelation(recordType, (Type) typedSupertypeNode.value());
        }

        return new TypeCheckResult<>(typedNode, newContext);
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

    private static TypeCheckStatementResult<TypedFunctionStatementNode> typeCheckReturn(UntypedReturnNode node, TypeCheckerContext context) {
        var expression = typeCheckExpression(node.expression(), context);

        if (context.returnType().isEmpty()) {
            throw new CannotReturnHereError(node.source());
        }
        var returnType = context.returnType().get();

        if (!isSubType(expression.type(), returnType)) {
            throw new UnexpectedTypeError(returnType, expression.type(), node.expression().source());
        }

        var typedNode = new TypedReturnNode(expression, node.source());

        return new TypeCheckStatementResult<>(typedNode, true, context);
    }

    private static TypedExpressionNode typeCheckStringLiteral(UntypedStringLiteralNode node) {
        return new TypedStringLiteralNode(node.value(), node.source());
    }

    private static TypeCheckerContext defineVariablesForTest(
        UntypedTestNode node,
        TypeCheckerContext context
    ) {
        return context;
    }

    private static TypeCheckResult<TypedNamespaceStatementNode> typeCheckTest(
        UntypedTestNode node,
        TypeCheckerContext context
    ) {
        var typedStatements = typeCheckFunctionStatements(
            node.body(),
            context.enterTest()
        ).value();

        var typedNode = new TypedTestNode(
            node.name(),
            typedStatements,
            node.source()
        );

        return new TypeCheckResult<>(typedNode, context);
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
        var type = resolveTypeLevelValue(node.value(), node.source(), context);
        return new TypedTypeLevelExpressionNode(type, node.source());
    }

    private static TypeCheckStatementResult<TypedFunctionStatementNode> typeCheckVar(
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
        return new TypeCheckStatementResult<>(typedNode, false, updatedContext);
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
