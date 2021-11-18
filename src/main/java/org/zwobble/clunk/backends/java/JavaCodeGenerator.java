package org.zwobble.clunk.backends.java;

import org.zwobble.clunk.ast.*;
import org.zwobble.clunk.backends.java.ast.JavaOrdinaryCompilationUnitNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordComponentNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordDeclarationNode;
import org.zwobble.clunk.backends.java.ast.JavaTypeReferenceNode;

import java.util.List;
import java.util.stream.Collectors;

public class JavaCodeGenerator {
    public static JavaRecordDeclarationNode compileRecord(RecordNode node) {
        var components = node.fields().stream()
            .map(field -> new JavaRecordComponentNode(compileStaticExpression(field.type()), field.name()))
            .collect(Collectors.toList());

        return new JavaRecordDeclarationNode(
            node.name(),
            components
        );
    }

    private static JavaTypeReferenceNode compileStaticExpression(StaticExpressionNode node) {
        return node.accept(new StaticExpressionNode.Visitor<JavaTypeReferenceNode>() {
            @Override
            public JavaTypeReferenceNode visit(StaticReferenceNode node) {
                return new JavaTypeReferenceNode(compileType(node.value()));
            }
        });
    }

    private static String compileType(String name) {
        return switch (name) {
            case "Int" -> "int";
            default -> name;
        };
    }

    public static List<JavaOrdinaryCompilationUnitNode> compileNamespace(NamespaceNode node) {
        return node.statements().stream()
            .map(statement -> compileNamespaceStatement(node, statement))
            .collect(Collectors.toList());
    }

    private static JavaOrdinaryCompilationUnitNode compileNamespaceStatement(NamespaceNode namespace, NamespaceStatementNode statement) {
        return new JavaOrdinaryCompilationUnitNode(
            String.join(".", namespace.name()),
            statement.accept(new NamespaceStatementNode.Visitor<JavaRecordDeclarationNode>() {
                @Override
                public JavaRecordDeclarationNode visit(RecordNode node) {
                    return compileRecord(node);
                }
            })
        );
    }
}
