package org.zwobble.clunk.backends.java;

import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.ast.typed.TypedNamespaceStatementNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.typed.TypedStaticExpressionNode;
import org.zwobble.clunk.backends.java.ast.JavaOrdinaryCompilationUnitNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordComponentNode;
import org.zwobble.clunk.backends.java.ast.JavaRecordDeclarationNode;
import org.zwobble.clunk.backends.java.ast.JavaTypeReferenceNode;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

import java.util.List;
import java.util.stream.Collectors;

public class JavaCodeGenerator {
    public static JavaRecordDeclarationNode compileRecord(TypedRecordNode node) {
        var components = node.fields().stream()
            .map(field -> new JavaRecordComponentNode(compileStaticExpression(field.type()), field.name()))
            .collect(Collectors.toList());

        return new JavaRecordDeclarationNode(
            node.name(),
            components
        );
    }

    private static JavaTypeReferenceNode compileStaticExpression(TypedStaticExpressionNode node) {
        return new JavaTypeReferenceNode(compileType(node.type()));
    }

    private static String compileType(Type type) {
        if (type == IntType.INSTANCE) {
            return "int";
        } else if (type == StringType.INSTANCE) {
            return "String";
        } else {
            throw new RuntimeException("TODO");
        }
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
}
