package org.zwobble.clunk.backends.typescript.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.typescript.ast.*;

public class TypeScriptSerialiser {
    private static void serialiseBoolLiteral(TypeScriptBoolLiteralNode node, CodeBuilder builder) {
        builder.append(node.value() ? "true" : "false");
    }

    public static void serialiseExpression(TypeScriptExpressionNode node, CodeBuilder builder) {
        node.accept(new TypeScriptExpressionNode.Visitor<Void>() {
            @Override
            public Void visit(TypeScriptBoolLiteralNode node) {
                serialiseBoolLiteral(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptStringLiteralNode node) {
                serialiseStringLiteral(node, builder);
                return null;
            }
        });
    }

    public static void serialiseInterfaceDeclaration(TypeScriptInterfaceDeclarationNode node, CodeBuilder builder) {
        builder.append("interface ");
        builder.append(node.name());
        builder.append(" {");
        builder.newLine();
        builder.indent();
        for (var field : node.fields()) {
            builder.append("readonly ");
            builder.append(field.name());
            builder.append(": ");
            serialiseReference(field.type(), builder);
            builder.append(";");
            builder.newLine();
        }
        builder.dedent();
        builder.append("}");
    }

    public static void serialiseModule(TypeScriptModuleNode node, CodeBuilder builder) {
        var isFirst = true;
        for (var statement : node.statements()) {
            if (!isFirst) {
                builder.newLine();
                builder.newLine();
            }

            serialiseInterfaceDeclaration(statement, builder);

            isFirst = false;
        }
    }

    public static void serialiseReference(TypeScriptReferenceNode node, CodeBuilder builder) {
        builder.append(node.name());
    }

    private static void serialiseStringLiteral(TypeScriptStringLiteralNode node, CodeBuilder builder) {
        builder.append("\"");
        var escapedValue = node.value()
            .replace("\\", "\\\\")
            .replace("\b", "\\b")
            .replace("\t", "\\t")
            .replace("\n", "\\n")
            .replace("\013", "\\v")
            .replace("\f", "\\f")
            .replace("\r", "\\r")
            .replace("\"", "\\\"");
        builder.append(escapedValue);
        builder.append("\"");
    }
}
