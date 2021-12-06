package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.ast.untyped.*;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

import java.util.stream.Collectors;

public class TypeChecker {
    private static TypedExpressionNode typeCheckBoolLiteral(UntypedBoolLiteralNode node) {
        return new TypedBoolLiteralNode(node.value(), node.source());
    }

    public static TypedExpressionNode typeCheckExpression(UntypedExpressionNode node) {
        return node.accept(new UntypedExpressionNode.Visitor<TypedExpressionNode>() {
            @Override
            public TypedExpressionNode visit(UntypedBoolLiteralNode node) {
                return typeCheckBoolLiteral(node);
            }

            @Override
            public TypedExpressionNode visit(UntypedStringLiteralNode node) {
                return typeCheckStringLiteral(node);
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

    private static TypedNamespaceStatementNode typeCheckNamespaceStatement(UntypedNamespaceStatementNode node) {
        return node.accept(new UntypedNamespaceStatementNode.Visitor<TypedNamespaceStatementNode>() {
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

    private static Type resolveType(String value) {
        return switch (value) {
            case "Bool" -> BoolType.INSTANCE;
            case "Int" -> IntType.INSTANCE;
            case "String" -> StringType.INSTANCE;
            default -> throw new RuntimeException("TODO");
        };
    }
}
