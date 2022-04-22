package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.ast.untyped.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.zwobble.clunk.types.Types.isSubType;

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
                throw new RuntimeException("TODO: Not implemented");
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

    private static TypeCheckFunctionStatementResult typeCheckExpressionStatement(
        UntypedExpressionStatementNode node,
        TypeCheckerContext context
    ) {
        var typedExpression = typeCheckExpression(node.expression(), context);
        var typedStatement = new TypedExpressionStatementNode(typedExpression, node.source());
        return new TypeCheckFunctionStatementResult(typedStatement, context);
    }

    private static TypedNamespaceStatementNode typeCheckFunction(
        UntypedFunctionNode node,
        TypeCheckerContext context
    ) {
        var returnType = typeCheckStaticExpressionNode(node.returnType(), context);

        var typedStatements = typeCheckFunctionStatements(
            node.body(),
            context.enterFunction(returnType.type())
        );

        return new TypedFunctionNode(
            node.name(),
            node.params().stream().map(param -> typeCheckParam(param, context)).toList(),
            returnType,
            typedStatements,
            node.source()
        );
    }

    public static TypeCheckFunctionStatementResult typeCheckFunctionStatement(
        UntypedFunctionStatementNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedFunctionStatementNode.Visitor<TypeCheckFunctionStatementResult>() {
            @Override
            public TypeCheckFunctionStatementResult visit(UntypedExpressionStatementNode node) {
                return typeCheckExpressionStatement(node, context);
            }

            @Override
            public TypeCheckFunctionStatementResult visit(UntypedReturnNode node) {
                return typeCheckReturn(node, context);
            }

            @Override
            public TypeCheckFunctionStatementResult visit(UntypedVarNode node) {
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

    public static TypedNamespaceNode typeCheckNamespace(
        UntypedNamespaceNode node,
        TypeCheckerContext context
    ) {
        return new TypedNamespaceNode(
            node.name(),
            node.statements().stream()
                .map(statement -> typeCheckNamespaceStatement(statement, context))
                .collect(Collectors.toList()),
            node.source()
        );
    }

    public static TypedNamespaceStatementNode typeCheckNamespaceStatement(
        UntypedNamespaceStatementNode node,
        TypeCheckerContext context
    ) {
        return node.accept(new UntypedNamespaceStatementNode.Visitor<TypedNamespaceStatementNode>() {
            @Override
            public TypedNamespaceStatementNode visit(UntypedFunctionNode node) {
                return typeCheckFunction(node, context);
            }

            @Override
            public TypedNamespaceStatementNode visit(UntypedRecordNode node) {
                return typeCheckRecord(node, context);
            }

            @Override
            public TypedNamespaceStatementNode visit(UntypedTestNode node) {
                return typeCheckTest(node, context);
            }
        });
    }

    public static TypedRecordNode typeCheckRecord(
        UntypedRecordNode node,
        TypeCheckerContext context
    ) {
        return new TypedRecordNode(
            node.name(),
            node.fields().stream()
                .map(field -> typeCheckRecordField(field, context))
                .collect(Collectors.toList()),
            node.source()
        );
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
        return new TypedReferenceNode(node.name(), context.typeOf(node.name()), node.source());
    }

    private static TypeCheckFunctionStatementResult typeCheckReturn(UntypedReturnNode node, TypeCheckerContext context) {
        var expression = typeCheckExpression(node.expression(), context);

        if (context.returnType().isEmpty()) {
            throw new CannotReturnHereError(node.source());
        }
        var returnType = context.returnType().get();

        if (!isSubType(expression.type(), returnType)) {
            throw new UnexpectedTypeError(returnType, expression.type(), node.expression().source());
        }

        var typedNode = new TypedReturnNode(expression, node.source());

        return new TypeCheckFunctionStatementResult(typedNode, context);
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
        var type = context.resolveType(node.value());
        return new TypedStaticExpressionNode(type, node.source());
    }

    private static TypedExpressionNode typeCheckStringLiteral(UntypedStringLiteralNode node) {
        return new TypedStringLiteralNode(node.value(), node.source());
    }

    private static TypedNamespaceStatementNode typeCheckTest(
        UntypedTestNode node,
        TypeCheckerContext context
    ) {
        var typedStatements = typeCheckFunctionStatements(
            node.body(),
            context.enterTest()
        );

        return new TypedTestNode(
            node.name(),
            typedStatements,
            node.source()
        );
    }

    private static TypeCheckFunctionStatementResult typeCheckVar(
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
        return new TypeCheckFunctionStatementResult(typedNode, updatedContext);
    }
}
