package org.zwobble.clunk.backends.typescript.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.typescript.ast.*;

import java.util.List;

import static org.zwobble.clunk.util.Iterables.forEachInterspersed;

public class TypeScriptSerialiser {
    private static void serialiseBlankLine(TypeScriptBlankLineNode node, CodeBuilder builder) {
        builder.newLine();
    }

    private static void serialiseBoolLiteral(TypeScriptBoolLiteralNode node, CodeBuilder builder) {
        builder.append(node.value() ? "true" : "false");
    }

    private static void serialiseBlock(List<TypeScriptStatementNode> statements, CodeBuilder builder) {
        builder.append(" {");
        builder.newLine();
        builder.indent();
        for (var statement : statements) {
            serialiseStatement(statement, builder);
        }
        builder.dedent();
        builder.append("}");
    }

    private static void serialiseCall(TypeScriptCallNode node, CodeBuilder builder) {
        builder.append("(");
        serialiseExpression(node.receiver(), builder);
        builder.append(")");

        builder.append("(");
        forEachInterspersed(
            node.args(),
            arg -> serialiseExpression(arg, builder),
            () -> builder.append(", ")
        );
        builder.append(")");
    }

    private static void serialiseClassDeclaration(TypeScriptClassDeclarationNode node, CodeBuilder builder) {
        builder.append("class ");
        builder.append(node.name());
        builder.append(" {");
        builder.indent();
        builder.newLine();

        for (var field : node.fields()) {
            builder.append("readonly ");
            builder.append(field.name());
            builder.append(": ");
            serialiseExpression(field.type(), builder);
            builder.append(";");
            builder.newLine();
        }

        if (!node.fields().isEmpty()) {
            builder.newLine();
            builder.append("constructor(");
            forEachInterspersed(
                node.fields(),
                field -> {
                    builder.append(field.name());
                    builder.append(": ");
                    serialiseExpression(field.type(), builder);
                },
                () -> builder.append(", ")
            );
            builder.append(") {");
            builder.newLine();
            builder.indent();
            for (var field : node.fields()) {
                builder.append("this.");
                builder.append(field.name());
                builder.append(" = ");
                builder.append(field.name());
                builder.append(";");
                builder.newLine();
            }
            builder.dedent();
            builder.append("}");
            builder.newLine();
        }

        builder.dedent();
        builder.append("}");
        builder.newLine();
    }

    private static void serialiseConstructedType(TypeScriptConstructedTypeNode node, CodeBuilder builder) {
        builder.append("(");
        serialiseExpression(node.receiver(), builder);
        builder.append(")");

        builder.append("<");
        forEachInterspersed(
            node.args(),
            arg -> serialiseExpression(arg, builder),
            () -> builder.append(", ")
        );
        builder.append(">");
    }

    private static void serialiseEnumDeclaration(
        TypeScriptEnumDeclarationNode node,
        CodeBuilder builder
    ) {
        builder.append("enum ");
        builder.append(node.name());
        builder.append(" {");
        builder.newLine();
        builder.indent();
        for (var member : node.members()) {
            builder.append(member);
            builder.append(",");
            builder.newLine();
        }
        builder.dedent();
        builder.append("}");
        builder.newLine();
    }

    public static void serialiseExpression(TypeScriptExpressionNode node, CodeBuilder builder) {
        node.accept(new TypeScriptExpressionNode.Visitor<Void>() {
            @Override
            public Void visit(TypeScriptBoolLiteralNode node) {
                serialiseBoolLiteral(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptCallNode node) {
                serialiseCall(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptConstructedTypeNode node) {
                serialiseConstructedType(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptUnionNode node) {
                serialiseUnion(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptNumberLiteralNode node) {
                serialiseNumberLiteral(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptFunctionExpressionNode node) {
                serialiseFunctionExpression(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptPropertyAccessNode node) {
                serialisePropertyAccess(node, builder);
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

    private static void serialiseExpressionStatement(TypeScriptExpressionStatementNode node, CodeBuilder builder) {
        serialiseExpression(node.expression(), builder);
        builder.append(";");
        builder.newLine();
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
        serialiseBlock(node.body(), builder);
    }

    private static void serialiseFunctionExpression(TypeScriptFunctionExpressionNode node, CodeBuilder builder) {
        builder.append("function ");
        builder.append("(");
        forEachInterspersed(
            node.params(),
            param -> serialiseParam(param, builder),
            () -> builder.append(", ")
        );
        builder.append(") {");
        builder.newLine();
        builder.indent();
        for (var statement : node.body()) {
            serialiseStatement(statement, builder);
        }
        builder.dedent();
        builder.append("}");
    }

    private static void serialiseNumberLiteral(TypeScriptNumberLiteralNode node, CodeBuilder builder) {
        if ((int)node.value() == node.value()) {
            builder.append(Integer.toString((int)node.value()));
        } else {
            builder.append(Double.toString(node.value()));
        }
    }

    private static void serialiseParam(TypeScriptParamNode node, CodeBuilder builder) {
        builder.append(node.name());
        builder.append(": ");
        serialiseExpression(node.type(), builder);
    }

    private static void serialiseIfStatement(TypeScriptIfStatementNode node, CodeBuilder builder) {
        builder.append("if (");
        var firstConditionalBranch = node.conditionalBranches().get(0);
        serialiseExpression(firstConditionalBranch.condition(), builder);
        builder.append(")");
        serialiseBlock(firstConditionalBranch.body(), builder);

        node.conditionalBranches().stream().skip(1).forEachOrdered(conditionalBranch -> {
            builder.append(" else if (");
            serialiseExpression(conditionalBranch.condition(), builder);
            builder.append(")");
            serialiseBlock(conditionalBranch.body(), builder);
        });

        if (node.elseBody().size() > 0) {
            builder.append(" else");
            serialiseBlock(node.elseBody(), builder);
        }

        builder.newLine();
    }

    private static void serialiseImport(TypeScriptImportNode node, CodeBuilder builder) {
        builder.append("import ");
        builder.append("{");
        forEachInterspersed(
            node.exports(),
            export -> builder.append(export),
            () -> builder.append(", ")
        );
        builder.append("}");
        builder.append(" from ");
        serialiseStringLiteral(node.module(), builder);
        builder.append(";");
        builder.newLine();
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
            serialiseExpression(field.type(), builder);
            builder.append(";");
            builder.newLine();
        }
        builder.dedent();
        builder.append("}");
        builder.newLine();
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
        for (var statement : node.statements()) {
            serialiseStatement(statement, builder);
        }
    }

    private static void serialisePropertyAccess(TypeScriptPropertyAccessNode node, CodeBuilder builder) {
        builder.append("(");
        serialiseExpression(node.receiver(), builder);
        builder.append(")");
        builder.append(".");
        builder.append(node.propertyName());
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
            public Void visit(TypeScriptBlankLineNode node) {
                serialiseBlankLine(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptClassDeclarationNode node) {
                serialiseClassDeclaration(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptEnumDeclarationNode node) {
                serialiseEnumDeclaration(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptExpressionStatementNode node) {
                serialiseExpressionStatement(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptFunctionDeclarationNode node) {
                serialiseFunctionDeclaration(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptIfStatementNode node) {
                serialiseIfStatement(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptImportNode node) {
                serialiseImport(node, builder);
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

            @Override
            public Void visit(TypeScriptTypeDeclarationNode node) {
                serialiseTypeDeclaration(node, builder);
                return null;
            }
        });
    }

    private static void serialiseStringLiteral(TypeScriptStringLiteralNode node, CodeBuilder builder) {
        serialiseStringLiteral(node.value(), builder);
    }

    private static void serialiseStringLiteral(String value, CodeBuilder builder) {
        builder.append("\"");
        var escapedValue = value
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

    private static void serialiseTypeDeclaration(TypeScriptTypeDeclarationNode node, CodeBuilder builder) {
        builder.append("type ");
        builder.append(node.name());
        builder.append(" = ");
        serialiseExpression(node.value(), builder);
        builder.append(";");
        builder.newLine();
    }

    private static void serialiseUnion(TypeScriptUnionNode node, CodeBuilder builder) {
        forEachInterspersed(
            node.members(),
            member -> serialiseExpression(member, builder),
            () -> builder.append(" | ")
        );
    }
}
