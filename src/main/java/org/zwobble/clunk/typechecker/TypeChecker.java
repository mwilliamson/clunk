package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.ast.untyped.*;
import org.zwobble.clunk.errors.SourceError;
import org.zwobble.clunk.types.InterfaceType;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Types;

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
            typeCheckStaticExpressionNode(node.type(), context),
            node.source()
        );
    }

    private static TypedExpressionNode typeCheckBoolLiteral(UntypedBoolLiteralNode node) {
        return new TypedBoolLiteralNode(node.value(), node.source());
    }

    private static TypedExpressionNode typeCheckCall(UntypedCallNode node, TypeCheckerContext context) {
        var receiver = typeCheckExpression(node.receiver(), context);

        // TODO: handle not a StaticFunctionType
        var receiverType = (StaticFunctionType) receiver.type();

        if (node.positionalArgs().size() != receiverType.positionalParams().size()) {
            throw new WrongNumberOfArgumentsError(
                receiverType.positionalParams().size(),
                node.positionalArgs().size(),
                node.source()
            );
        }
        var typedPositionalArgs = node.positionalArgs().stream().map(arg -> typeCheckExpression(arg, context)).toList();

        for (var argIndex = 0; argIndex < receiverType.positionalParams().size(); argIndex++) {
            var paramType = receiverType.positionalParams().get(argIndex);
            var argNode = typedPositionalArgs.get(argIndex);
            var argType = argNode.type();
            if (!isSubType(argType, paramType)) {
                throw new UnexpectedTypeError(paramType, argType, argNode.source());
            }
        }
        return new TypedCallNode(
            receiver,
            typedPositionalArgs,
            receiverType.returnType(),
            node.source()
        );
    }

    private static TypedConditionalBranchNode typeCheckConditionalBranch(
        UntypedConditionalBranchNode node,
        TypeCheckerContext context
    ) {
        var typedConditionNode = typeCheckExpression(node.condition(), context);

        if (!typedConditionNode.type().equals(Types.BOOL)) {
            throw new UnexpectedTypeError(Types.BOOL, typedConditionNode.type(), typedConditionNode.source());
        }

        return new TypedConditionalBranchNode(
            typedConditionNode,
            typeCheckFunctionStatements(node.body(), context),
            node.source()
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
                throw new RuntimeException("TODO: Not implemented");
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

    private static TypeCheckResult<TypedFunctionStatementNode> typeCheckExpressionStatement(
        UntypedExpressionStatementNode node,
        TypeCheckerContext context
    ) {
        var typedExpression = typeCheckExpression(node.expression(), context);
        var typedStatement = new TypedExpressionStatementNode(typedExpression, node.source());
        return new TypeCheckResult<>(typedStatement, context);
    }

    private static TypeCheckResult<TypedNamespaceStatementNode> typeCheckFunction(
        UntypedFunctionNode node,
        TypeCheckerContext context
    ) {
        var returnType = typeCheckStaticExpressionNode(node.returnType(), context);

        var typedStatements = typeCheckFunctionStatements(
            node.body(),
            context.enterFunction(returnType.type())
        );

        var typedNode = new TypedFunctionNode(
            node.name(),
            node.params().stream().map(param -> typeCheckParam(param, context)).toList(),
            returnType,
            typedStatements,
            node.source()
        );

        return new TypeCheckResult<>(typedNode, context);
    }

    public static TypeCheckResult<TypedFunctionStatementNode> typeCheckFunctionStatement(
        UntypedFunctionStatementNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedFunctionStatementNode.Visitor<>() {
            @Override
            public TypeCheckResult<TypedFunctionStatementNode> visit(UntypedExpressionStatementNode node) {
                return typeCheckExpressionStatement(node, context);
            }

            @Override
            public TypeCheckResult<TypedFunctionStatementNode> visit(UntypedIfStatementNode node) {
                return typeCheckIfStatement(node, context);
            }

            @Override
            public TypeCheckResult<TypedFunctionStatementNode> visit(UntypedReturnNode node) {
                return typeCheckReturn(node, context);
            }

            @Override
            public TypeCheckResult<TypedFunctionStatementNode> visit(UntypedVarNode node) {
                return typeCheckVar(node, context);
            }
        });
    }

    private static List<TypedFunctionStatementNode> typeCheckFunctionStatements(
        List<UntypedFunctionStatementNode> body,
        TypeCheckerContext context
    ) {
        var typedStatements = new ArrayList<TypedFunctionStatementNode>();

        for (var statement : body) {
            var statementResult = typeCheckFunctionStatement(statement, context);
            context = statementResult.context();
            typedStatements.add(statementResult.typedNode());
        }

        return typedStatements;
    }

    private static TypeCheckResult<TypedFunctionStatementNode> typeCheckIfStatement(
        UntypedIfStatementNode node,
        TypeCheckerContext context
    ) {
        var typedNode = new TypedIfStatementNode(
            node.conditionalBranches().stream()
                .map(branch -> typeCheckConditionalBranch(branch, context))
                .toList(),
            typeCheckFunctionStatements(node.elseBody(), context),
            node.source()
        );

        return new TypeCheckResult<>(typedNode, context);
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

            var newContext = context.updateType(fieldName, importType);

            var typedNode = new TypedImportNode(import_.namespaceName(), import_.fieldName(), importType, import_.source());

            return new TypeCheckImportResult(typedNode, newContext);
        } else {
            throw new RuntimeException("TODO");
        }
    }

    private static TypeCheckResult<TypedNamespaceStatementNode> typeCheckInterface(UntypedInterfaceNode node, TypeCheckerContext context) {
        // TODO: handle missing namespace name
        var interfaceType = new InterfaceType(context.namespaceName().get(), node.name());

        var typedNode = new TypedInterfaceNode(node.name(), interfaceType, node.source());

        context = context.updateType(node.name(), metaType(interfaceType));

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
        // TODO: revert to original environment (probably elsewhere too)
        return new TypeCheckResult<>(typedNode, context);
    }

    public static TypeCheckResult<TypedNamespaceStatementNode> typeCheckNamespaceStatement(
        UntypedNamespaceStatementNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedNamespaceStatementNode.Visitor<TypeCheckResult<TypedNamespaceStatementNode>>() {
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

    private static TypeCheckResult<TypedNamespaceStatementNode> typeCheckRecord(
        UntypedRecordNode node,
        TypeCheckerContext context
    ) {
        var recordType = new RecordType(context.namespaceName().get(), node.name());

        var typedNode = new TypedRecordNode(
            node.name(),
            node.fields().stream()
                .map(field -> typeCheckRecordField(field, context))
                .collect(Collectors.toList()),
            recordType,
            node.source()
        );

        var supertypes = node.supertypes().stream()
            .map(untypedSupertypeNode -> {
                var typedSupertypeNode = typeCheckStaticExpressionNode(untypedSupertypeNode, context);
                if (typedSupertypeNode.type() instanceof InterfaceType supertype) {
                    if (!supertype.namespaceName().equals(context.namespaceName().get())) {
                        throw new CannotExtendSealedInterfaceFromDifferentNamespaceError(untypedSupertypeNode.source());
                    }
                } else {
                    throw new CannotExtendFinalTypeError(untypedSupertypeNode.source());
                }
                return typedSupertypeNode;
            })
            .toList();

        // TODO: handle missing namespace name

        var newContext = context;

        for (var supertype : supertypes) {
            newContext = newContext.addSubtypeRelation(recordType, supertype.type());
        }

        return new TypeCheckResult<>(typedNode, newContext);
    }

    private static TypedRecordFieldNode typeCheckRecordField(
        UntypedRecordFieldNode node,
        TypeCheckerContext context
    ) {
        return new TypedRecordFieldNode(
            node.name(),
            typeCheckStaticExpressionNode(node.type(), context),
            node.source()
        );
    }

    private static TypedExpressionNode typeCheckReference(UntypedReferenceNode node, TypeCheckerContext context) {
        var type = context.typeOf(node.name(), node.source());
        return new TypedReferenceNode(node.name(), type, node.source());
    }

    private static TypeCheckResult<TypedFunctionStatementNode> typeCheckReturn(UntypedReturnNode node, TypeCheckerContext context) {
        var expression = typeCheckExpression(node.expression(), context);

        if (context.returnType().isEmpty()) {
            throw new CannotReturnHereError(node.source());
        }
        var returnType = context.returnType().get();

        if (!isSubType(expression.type(), returnType)) {
            throw new UnexpectedTypeError(returnType, expression.type(), node.expression().source());
        }

        var typedNode = new TypedReturnNode(expression, node.source());

        return new TypeCheckResult<>(typedNode, context);
    }

    private static TypedStaticExpressionNode typeCheckStaticExpressionNode(
        UntypedStaticExpressionNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedStaticExpressionNode.Visitor<TypedStaticExpressionNode>() {
            @Override
            public TypedStaticExpressionNode visit(UntypedStaticReferenceNode node) {
                return typeCheckStaticReferenceNode(node, context);
            }
        });
    }

    public static TypedStaticExpressionNode typeCheckStaticReferenceNode(
        UntypedStaticReferenceNode node,
        TypeCheckerContext context
    ) {
        var type = context.resolveType(node.value(), node.source());
        return new TypedStaticExpressionNode(type, node.source());
    }

    private static TypedExpressionNode typeCheckStringLiteral(UntypedStringLiteralNode node) {
        return new TypedStringLiteralNode(node.value(), node.source());
    }

    private static TypeCheckResult<TypedNamespaceStatementNode> typeCheckTest(
        UntypedTestNode node,
        TypeCheckerContext context
    ) {
        var typedStatements = typeCheckFunctionStatements(
            node.body(),
            context.enterTest()
        );

        var typedNode = new TypedTestNode(
            node.name(),
            typedStatements,
            node.source()
        );

        return new TypeCheckResult<>(typedNode, context);
    }

    private static TypeCheckResult<TypedFunctionStatementNode> typeCheckVar(
        UntypedVarNode node,
        TypeCheckerContext context
    ) {
        var typedExpression = typeCheckExpression(node.expression(), context);
        var typedNode = new TypedVarNode(
            node.name(),
            typedExpression,
            node.source()
        );
        var updatedContext = context.updateType(node.name(), typedExpression.type());
        return new TypeCheckResult<>(typedNode, updatedContext);
    }
}
