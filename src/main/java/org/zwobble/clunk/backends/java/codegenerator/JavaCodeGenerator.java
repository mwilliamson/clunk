package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.java.ast.*;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

import java.util.List;
import java.util.stream.Collectors;

public class JavaCodeGenerator {
    public static JavaStringLiteralNode compileExpression(TypedExpressionNode node) {
        return node.accept(new TypedExpressionNode.Visitor<JavaStringLiteralNode>() {
            @Override
            public JavaStringLiteralNode visit(TypedStringLiteralNode node) {
                return compileStringLiteral(node);
            }
        });
    }

    public static JavaRecordDeclarationNode compileRecord(TypedRecordNode node) {
        var components = node.fields().stream()
            .map(field -> new JavaRecordComponentNode(compileStaticExpression(field.type()), field.name()))
            .collect(Collectors.toList());

        return new JavaRecordDeclarationNode(
            node.name(),
            components
        );
    }

    public static List<JavaOrdinaryCompilationUnitNode> compileNamespace(TypedNamespaceNode node) {
        return node.statements().stream()
            .map(statement -> compileNamespaceStatement(node, statement))
            .collect(Collectors.toList());
    }

    private static JavaOrdinaryCompilationUnitNode compileNamespaceStatement(TypedNamespaceNode namespace, TypedNamespaceStatementNode statement) {
        return new JavaOrdinaryCompilationUnitNode(
            String.join(".", namespace.name()),
            statement.accept(new TypedNamespaceStatementNode.Visitor<JavaRecordDeclarationNode>() {
                @Override
                public JavaRecordDeclarationNode visit(TypedRecordNode node) {
                    return compileRecord(node);
                }
            })
        );
    }

    public static JavaTypeReferenceNode compileStaticExpression(TypedStaticExpressionNode node) {
        return new JavaTypeReferenceNode(compileType(node.type()));
    }

    private static JavaStringLiteralNode compileStringLiteral(TypedStringLiteralNode node) {
        return new JavaStringLiteralNode(node.value());
    }

    private static String compileType(Type type) {
        if (type == BoolType.INSTANCE) {
            return "boolean";
        } else if (type == IntType.INSTANCE) {
            return "int";
        } else if (type == StringType.INSTANCE) {
            return "String";
        } else {
            throw new RuntimeException("TODO");
        }
    }
}
