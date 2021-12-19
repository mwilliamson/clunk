package org.zwobble.clunk.backends.typescript.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.typescript.ast.*;

import static org.zwobble.clunk.util.Iterables.forEachInterspersed;

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
            public Void visit(TypeScriptReferenceNode node) {
                serialiseReference(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptStringLiteralNode node) {
                serialiseStringLiteral(node, builder);
                return null;
            }
        });
    }

    private static void serialiseFunctionDeclaration(TypeScriptFunctionDeclarationNode node, CodeBuilder builder) {
        builder.append("function ");
        builder.append(node.name());
        builder.append("(");
        forEachInterspersed(
            node.params(),
            param -> serialiseParam(param, builder),
            () -> builder.append(", ")
        );
        builder.append("): ");
        serialiseExpression(node.returnType(), builder);
        builder.append(" {");
        builder.newLine();
        builder.indent();
        for (var statement : node.body()) {
            serialiseStatement(statement, builder);
        }
        builder.dedent();
        builder.append("}");
    }

    private static void serialiseParam(TypeScriptParamNode node, CodeBuilder builder) {
        builder.append(node.name());
        builder.append(": ");
        serialiseExpression(node.type(), builder);
    }

    private static void serialiseInterfaceDeclaration(TypeScriptInterfaceDeclarationNode node, CodeBuilder builder) {
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

    private static void serialiseLet(TypeScriptLetNode node, CodeBuilder builder) {
        builder.append("let ");
        builder.append(node.name());
        builder.append(" = ");
        serialiseExpression(node.expression(), builder);
        builder.append(";");
        builder.newLine();
    }

    public static void serialiseModule(TypeScriptModuleNode node, CodeBuilder builder) {
        var isFirst = true;
        for (var statement : node.statements()) {
            if (!isFirst) {
                builder.newLine();
                builder.newLine();
            }

            serialiseStatement(statement, builder);

            isFirst = false;
        }
    }

    private static void serialiseReference(TypeScriptReferenceNode node, CodeBuilder builder) {
        builder.append(node.name());
    }

    private static void serialiseReturn(TypeScriptReturnNode node, CodeBuilder builder) {
        builder.append("return ");
        serialiseExpression(node.expression(), builder);
        builder.append(";");
        builder.newLine();
    }

    public static void serialiseStatement(TypeScriptStatementNode node, CodeBuilder builder) {
        node.accept(new TypeScriptStatementNode.Visitor<Void>() {
            @Override
            public Void visit(TypeScriptFunctionDeclarationNode node) {
                serialiseFunctionDeclaration(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptInterfaceDeclarationNode node) {
                serialiseInterfaceDeclaration(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptLetNode node) {
                serialiseLet(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptReturnNode node) {
                serialiseReturn(node, builder);
                return null;
            }
        });
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
