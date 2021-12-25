package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.ast.untyped.*;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.zwobble.clunk.types.Types.isSubType;

public class TypeChecker {
    private static TypedParamNode typeCheckParam(UntypedParamNode node) {
        return new TypedParamNode(
            node.name(),
            typeCheckStaticExpressionNode(node.type()),
            node.source()
        );
    }

    private static TypedExpressionNode typeCheckBoolLiteral(UntypedBoolLiteralNode node) {
        return new TypedBoolLiteralNode(node.value(), node.source());
    }

    public static TypedExpressionNode typeCheckExpression(
        UntypedExpressionNode node,
        TypeCheckerFunctionContext context
    ) {
        return node.accept(new UntypedExpressionNode.Visitor<TypedExpressionNode>() {
            @Override
            public TypedExpressionNode visit(UntypedBoolLiteralNode node) {
                return typeCheckBoolLiteral(node);
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

    private static TypedNamespaceStatementNode typeCheckFunction(UntypedFunctionNode node) {
        var returnType = typeCheckStaticExpressionNode(node.returnType());

        var context = TypeCheckerFunctionContext.enter(returnType.type());
        var typedStatements = new ArrayList<TypedFunctionStatementNode>();

        for (var statement : node.body()) {
            var statementResult = typeCheckFunctionStatement(statement, context);
            context = statementResult.context();
            typedStatements.add(statementResult.typedNode());
        }

        return new TypedFunctionNode(
            node.name(),
            node.params().stream().map(param -> typeCheckParam(param)).toList(),
            returnType,
            typedStatements,
            node.source()
        );
    }

    public static TypeCheckFunctionStatementResult typeCheckFunctionStatement(
        UntypedFunctionStatementNode node,
        TypeCheckerFunctionContext context
    ) {
        return node.accept(new UntypedFunctionStatementNode.Visitor<TypeCheckFunctionStatementResult>() {
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

    public static TypedNamespaceNode typeCheckNamespace(UntypedNamespaceNode node) {
        return new TypedNamespaceNode(
            node.name(),
            node.statements().stream()
                .map(statement -> typeCheckNamespaceStatement(statement))
                .collect(Collectors.toList()),
            node.source()
        );
    }

    public static TypedNamespaceStatementNode typeCheckNamespaceStatement(UntypedNamespaceStatementNode node) {
        return node.accept(new UntypedNamespaceStatementNode.Visitor<TypedNamespaceStatementNode>() {
            @Override
            public TypedNamespaceStatementNode visit(UntypedFunctionNode node) {
                return typeCheckFunction(node);
            }

            @Override
            public TypedNamespaceStatementNode visit(UntypedRecordNode node) {
                return typeCheckRecord(node);
            }
        });
    }

    public static TypedRecordNode typeCheckRecord(UntypedRecordNode node) {
        return new TypedRecordNode(
            node.name(),
            node.fields().stream()
                .map(field -> typeCheckRecordField(field))
                .collect(Collectors.toList()),
            node.source()
        );
    }

    private static TypedRecordFieldNode typeCheckRecordField(UntypedRecordFieldNode node) {
        return new TypedRecordFieldNode(
            node.name(),
            typeCheckStaticExpressionNode(node.type()),
            node.source()
        );
    }

    private static TypedExpressionNode typeCheckReference(UntypedReferenceNode node, TypeCheckerFunctionContext context) {
        return new TypedReferenceNode(node.name(), context.typeOf(node.name()), node.source());
    }

    private static TypeCheckFunctionStatementResult typeCheckReturn(UntypedReturnNode node, TypeCheckerFunctionContext context) {
        var expression = typeCheckExpression(node.expression(), context);

        if (!isSubType(expression.type(), context.returnType())) {
            throw new UnexpectedTypeError(context.returnType(), expression.type(), node.expression().source());
        }

        var typedNode = new TypedReturnNode(expression, node.source());

        return new TypeCheckFunctionStatementResult(typedNode, context);
    }

    private static TypedStaticExpressionNode typeCheckStaticExpressionNode(UntypedStaticExpressionNode node) {
        return node.accept(new UntypedStaticExpressionNode.Visitor<TypedStaticExpressionNode>() {
            @Override
            public TypedStaticExpressionNode visit(UntypedStaticReferenceNode node) {
                return typeCheckStaticReferenceNode(node);
            }
        });
    }

    public static TypedStaticExpressionNode typeCheckStaticReferenceNode(UntypedStaticReferenceNode node) {
        var type = resolveType(node.value());
        return new TypedStaticExpressionNode(type, node.source());
    }

    private static TypedExpressionNode typeCheckStringLiteral(UntypedStringLiteralNode node) {
        return new TypedStringLiteralNode(node.value(), node.source());
    }

    private static TypeCheckFunctionStatementResult typeCheckVar(
        UntypedVarNode node,
        TypeCheckerFunctionContext context
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

    private static Type resolveType(String value) {
        return switch (value) {
            case "Bool" -> BoolType.INSTANCE;
            case "Int" -> IntType.INSTANCE;
            case "String" -> StringType.INSTANCE;
            default -> throw new RuntimeException("TODO");
        };
    }
}
