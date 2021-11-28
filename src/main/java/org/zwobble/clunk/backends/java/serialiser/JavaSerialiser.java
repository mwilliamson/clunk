package org.zwobble.clunk.backends.java.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.java.ast.*;

public class JavaSerialiser {
    public static void serialiseExpression(JavaExpressionNode node, CodeBuilder builder) {
        node.accept(new JavaExpressionNode.Visitor<Void>() {
            @Override
            public Void visit(JavaStringLiteralNode node) {
                serialiseStringLiteral(node, builder);
                return null;
            }
        });
    }

    public static void serialiseOrdinaryCompilationUnit(JavaOrdinaryCompilationUnitNode node, CodeBuilder builder) {
        serialisePackageDeclaration(node.packageDeclaration(), builder);
        builder.newLine();
        builder.newLine();
        serialiseRecordDeclaration(node.typeDeclaration(), builder);
    }

    private static void serialisePackageDeclaration(String packageDeclaration, CodeBuilder builder) {
        builder.append("package ");
        builder.append(packageDeclaration);
        builder.append(";");
    }

    public static void serialiseRecordDeclaration(JavaRecordDeclarationNode node, CodeBuilder builder) {
        builder.append("public record ");
        builder.append(node.name());
        builder.append("(");

        var first = true;
        for (var component : node.components()) {
            if (!first) {
                builder.append(", ");
            }
            serialiseTypeReference(component.type(), builder);
            builder.append(" ");
            builder.append(component.name());

            first = false;
        }

        builder.append(") {\n}");
    }

    private static void serialiseStringLiteral(JavaStringLiteralNode node, CodeBuilder builder) {
        builder.append("\"");
        var escapedValue = node.value()
            .replace("\\", "\\\\")
            .replace("\b", "\\b")
            .replace("\t", "\\t")
            .replace("\n", "\\n")
            .replace("\f", "\\f")
            .replace("\r", "\\r")
            .replace("\"", "\\\"");
        builder.append(escapedValue);
        builder.append("\"");
    }

    public static void serialiseTypeReference(JavaTypeReferenceNode node, CodeBuilder builder) {
        builder.append(node.name());
    }
}
