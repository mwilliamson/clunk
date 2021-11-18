package org.zwobble.clunk.backends.java;

import org.zwobble.clunk.ast.untyped.*;
import org.zwobble.clunk.backends.java.ast.JavaOrdinaryCompilationUnitNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordComponentNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordDeclarationNode;
import org.zwobble.clunk.backends.java.ast.JavaTypeReferenceNode;

import java.util.List;
import java.util.stream.Collectors;

public class JavaCodeGenerator {
    public static JavaRecordDeclarationNode compileRecord(UntypedRecordNode node) {
        var components = node.fields().stream()
            .map(field -> new JavaRecordComponentNode(compileStaticExpression(field.type()), field.name()))
            .collect(Collectors.toList());

        return new JavaRecordDeclarationNode(
            node.name(),
            components
        );
    }

    private static JavaTypeReferenceNode compileStaticExpression(UntypedStaticExpressionNode node) {
        return node.accept(new UntypedStaticExpressionNode.Visitor<JavaTypeReferenceNode>() {
            @Override
            public JavaTypeReferenceNode visit(UntypedStaticReferenceNode node) {
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

    public static List<JavaOrdinaryCompilationUnitNode> compileNamespace(UntypedNamespaceNode node) {
        return node.statements().stream()
            .map(statement -> compileNamespaceStatement(node, statement))
            .collect(Collectors.toList());
    }

    private static JavaOrdinaryCompilationUnitNode compileNamespaceStatement(UntypedNamespaceNode namespace, UntypedNamespaceStatementNode statement) {
        return new JavaOrdinaryCompilationUnitNode(
            String.join(".", namespace.name()),
            statement.accept(new UntypedNamespaceStatementNode.Visitor<JavaRecordDeclarationNode>() {
                @Override
                public JavaRecordDeclarationNode visit(UntypedRecordNode node) {
                    return compileRecord(node);
                }
            })
        );
    }
}
