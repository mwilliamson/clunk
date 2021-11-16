package org.zwobble.clunk.backends.java;

import org.zwobble.clunk.ast.RecordNode;
import org.zwobble.clunk.ast.StaticExpressionNode;
import org.zwobble.clunk.ast.StaticReferenceNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordComponentNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordDeclarationNode;
import org.zwobble.clunk.backends.java.ast.JavaTypeReferenceNode;

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
}
