package org.zwobble.clunk.backends.typescript.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.typescript.ast.*;

import java.util.List;
import java.util.Optional;

import static org.zwobble.clunk.util.Iterables.forEachInterspersed;

public class TypeScriptSerialiser {
    private static void serialiseAdd(TypeScriptAddNode node, CodeBuilder builder) {
        serialiseBinaryOperation("+", node, builder);
    }

    private static void serialiseArray(TypeScriptArrayNode node, CodeBuilder builder) {
        builder.append("[");
        forEachInterspersed(
            node.elements(),
            element -> serialiseExpression(element, builder, Optional.empty()),
            () -> builder.append(", ")
        );
        builder.append("]");
    }

    private static void serialiseBinaryOperation(String operator, TypeScriptBinaryOperationNode node, CodeBuilder builder) {
        serialiseExpression(node.left(), builder, Optional.of(node));
        builder.append(" ");
        builder.append(operator);
        builder.append(" ");
        serialiseExpression(node.right(), builder, Optional.of(node));
    }

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
        serialiseExpression(node.receiver(), builder, Optional.of(node));

        builder.append("(");
        forEachInterspersed(
            node.args(),
            arg -> serialiseExpression(arg, builder, Optional.empty()),
            () -> builder.append(", ")
        );
        builder.append(")");
    }

    private static void serialiseCallNew(TypeScriptCallNewNode node, CodeBuilder builder) {
        builder.append("new ");
        serialiseExpression(node.receiver(), builder, Optional.of(node));
        if (!node.typeArgs().isEmpty()) {
            builder.append("<");
            forEachInterspersed(
                node.typeArgs(),
                typeArg -> serialiseExpression(typeArg, builder, Optional.empty()),
                () -> builder.append(", ")
            );
            builder.append(">");
        }
        builder.append("(");
        forEachInterspersed(
            node.args(),
            arg -> serialiseExpression(arg, builder, Optional.empty()),
            () -> builder.append(", ")
        );
        builder.append(")");
    }

    private static void serialiseCast(TypeScriptCastNode node, CodeBuilder builder) {
        builder.append("((");
        serialiseExpression(node.expression(), builder, Optional.empty());
        builder.append(") as ");
        serialiseExpression(node.type(), builder, Optional.empty());
        builder.append(")");
    }

    private static void serialiseClassDeclaration(TypeScriptClassDeclarationNode node, CodeBuilder builder) {
        builder.append("class ");
        builder.append(node.name());
        builder.append(" {");
        builder.indent();
        builder.newLine();

        for (var field : node.fields()) {
            serialiseClassField(field, builder);
        }

        var variableFields = node.fields().stream()
            .filter(field -> field.constantValue().isEmpty())
            .toList();

        if (!variableFields.isEmpty()) {
            serialiseConstructor(variableFields, builder);
        }
        
        for (var declaration : node.body()) {
            serialiseClassBodyDeclaration(declaration, builder);
        }

        builder.dedent();
        builder.append("}");
        builder.newLine();
    }

    public static void serialiseClassBodyDeclaration(TypeScriptClassBodyDeclarationNode node, CodeBuilder builder) {
        node.accept(new TypeScriptClassBodyDeclarationNode.Visitor<Void>() {
            @Override
            public Void visit(TypeScriptBlankLineNode node) {
                serialiseBlankLine(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptFunctionDeclarationNode node) {
                serialiseMethod(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptGetterNode node) {
                serialiseGetter(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptSingleLineCommentNode node) {
                serialiseSingleLineComment(node, builder);
                return null;
            }
        });
    }

    private static void serialiseClassField(TypeScriptClassFieldNode field, CodeBuilder builder) {
        builder.append("readonly ");
        builder.append(field.name());
        builder.append(": ");
        serialiseExpression(field.type(), builder, Optional.empty());
        if (field.constantValue().isPresent()) {
            builder.append(" = ");
            serialiseExpression(field.constantValue().get(), builder, Optional.empty());
        }
        builder.append(";");
        builder.newLine();
    }

    private static void serialiseConstructor(List<TypeScriptClassFieldNode> variableFields, CodeBuilder builder) {
        builder.newLine();
        builder.append("constructor(");
        forEachInterspersed(
            variableFields,
            field -> {
                builder.append(field.name());
                builder.append(": ");
                serialiseExpression(field.type(), builder, Optional.empty());
            },
            () -> builder.append(", ")
        );
        builder.append(") {");
        builder.newLine();
        builder.indent();
        for (var field : variableFields) {
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

    private static void serialiseConstructedType(TypeScriptConstructedTypeNode node, CodeBuilder builder) {
        serialiseExpression(node.receiver(), builder, Optional.of(node));

        builder.append("<");
        forEachInterspersed(
            node.args(),
            arg -> serialiseExpression(arg, builder, Optional.empty()),
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

    private static void serialiseEquals(TypeScriptEqualsNode node, CodeBuilder builder) {
        serialiseBinaryOperation("==", node, builder);
    }

    private static void serialiseExport(TypeScriptExportNode node, CodeBuilder builder) {
        builder.append("export {");
        forEachInterspersed(
            node.names(),
            name -> builder.append(name),
            () -> builder.append(", ")
        );
        builder.append("};");
        builder.newLine();
    }

    public static void serialiseExpression(TypeScriptExpressionNode node, CodeBuilder builder, Optional<TypeScriptExpressionNode> parent) {
        var parenthesize = parent.isPresent() && node.precedence().ordinal() < parent.get().precedence().ordinal();
        if (parenthesize) {
            builder.append("(");
        }

        node.accept(new TypeScriptExpressionNode.Visitor<Void>() {
            @Override
            public Void visit(TypeScriptAddNode node) {
                serialiseAdd(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptArrayNode node) {
                serialiseArray(node, builder);
                return null;
            }

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
            public Void visit(TypeScriptCallNewNode node) {
                serialiseCallNew(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptCastNode node) {
                serialiseCast(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptConstructedTypeNode node) {
                serialiseConstructedType(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptEqualsNode node) {
                serialiseEquals(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptIndexNode node) {
                serialiseIndex(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptLogicalAndNode node) {
                serialiseLogicalAnd(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptLogicalNotNode node) {
                serialiseLogicalNot(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptLogicalOrNode node) {
                serialiseLogicalOr(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptNonNullAssertionNode node) {
                serialiseNonNullAssertion(node, builder);
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
            public Void visit(TypeScriptStrictEqualsNode node) {
                serialiseStrictEquals(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptStrictNotEqualNode node) {
                serialiseStrictNotEqual(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptStringLiteralNode node) {
                serialiseStringLiteral(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptSubtractNode node) {
                serialiseSubtract(node, builder);
                return null;
            }
        });

        if (parenthesize) {
            builder.append(")");
        }
    }

    private static void serialiseExpressionStatement(TypeScriptExpressionStatementNode node, CodeBuilder builder) {
        serialiseExpression(node.expression(), builder, Optional.empty());
        builder.append(";");
        builder.newLine();
    }

    private static void serialiseForOf(TypeScriptForOfNode node, CodeBuilder builder) {
        builder.append("for (let ");
        builder.append(node.targetName());
        builder.append(" of ");
        serialiseExpression(node.iterable(), builder, Optional.empty());
        builder.append(")");
        serialiseBlock(node.body(), builder);
        builder.newLine();
    }

    private static void serialiseFunctionDeclaration(TypeScriptFunctionDeclarationNode node, CodeBuilder builder) {
        builder.append("function ");
        builder.append(node.name());
        builder.append("(");
        serialiseParams(node.params(), builder);
        builder.append("): ");
        serialiseExpression(node.returnType(), builder, Optional.empty());
        serialiseBlock(node.body(), builder);
        builder.newLine();
    }

    private static void serialiseFunctionExpression(TypeScriptFunctionExpressionNode node, CodeBuilder builder) {
        builder.append("function ");
        builder.append("(");
        serialiseParams(node.params(), builder);
        builder.append(")");
        serialiseBlock(node.body(), builder);
    }

    private static void serialiseGetter(TypeScriptGetterNode node, CodeBuilder builder) {
        builder.append("get ");
        builder.append(node.name());
        builder.append("(): ");
        serialiseExpression(node.type(), builder, Optional.empty());
        serialiseBlock(node.body(), builder);
        builder.newLine();
    }

    private static void serialiseIndex(TypeScriptIndexNode node, CodeBuilder builder) {
        serialiseExpression(node.receiver(), builder, Optional.of(node));
        builder.append("[");
        serialiseExpression(node.index(), builder, Optional.empty());
        builder.append("]");
    }

    private static void serialiseLogicalAnd(TypeScriptLogicalAndNode node, CodeBuilder builder) {
        serialiseBinaryOperation("&&", node, builder);
    }

    private static void serialiseLogicalNot(TypeScriptLogicalNotNode node, CodeBuilder builder) {
        builder.append("!");
        serialiseExpression(node.operand(), builder, Optional.of(node));
    }

    private static void serialiseLogicalOr(TypeScriptLogicalOrNode node, CodeBuilder builder) {
        serialiseBinaryOperation("||", node, builder);
    }

    private static void serialiseMethod(TypeScriptFunctionDeclarationNode node, CodeBuilder builder) {
        builder.append(node.name());
        builder.append("");
        builder.append("(");
        serialiseParams(node.params(), builder);
        builder.append("): ");
        serialiseExpression(node.returnType(), builder, Optional.empty());
        serialiseBlock(node.body(), builder);
        builder.newLine();
    }

    private static void serialiseNonNullAssertion(
        TypeScriptNonNullAssertionNode node,
        CodeBuilder builder
    ) {
        serialiseExpression(node.operand(), builder, Optional.of(node));
        builder.append("!");
    }

    private static void serialiseNumberLiteral(TypeScriptNumberLiteralNode node, CodeBuilder builder) {
        if ((int)node.value() == node.value()) {
            builder.append(Integer.toString((int)node.value()));
        } else {
            builder.append(Double.toString(node.value()));
        }
    }

    private static void serialiseParams(List<TypeScriptParamNode> params, CodeBuilder builder) {
        forEachInterspersed(
            params,
            param -> serialiseParam(param, builder),
            () -> builder.append(", ")
        );
    }

    private static void serialiseParam(TypeScriptParamNode node, CodeBuilder builder) {
        builder.append(node.name());
        builder.append(": ");
        serialiseExpression(node.type(), builder, Optional.empty());
    }

    private static void serialiseIfStatement(TypeScriptIfStatementNode node, CodeBuilder builder) {
        builder.append("if (");
        var firstConditionalBranch = node.conditionalBranches().get(0);
        serialiseExpression(firstConditionalBranch.condition(), builder, Optional.empty());
        builder.append(")");
        serialiseBlock(firstConditionalBranch.body(), builder);

        node.conditionalBranches().stream().skip(1).forEachOrdered(conditionalBranch -> {
            builder.append(" else if (");
            serialiseExpression(conditionalBranch.condition(), builder, Optional.empty());
            builder.append(")");
            serialiseBlock(conditionalBranch.body(), builder);
        });

        if (node.elseBody().size() > 0) {
            builder.append(" else");
            serialiseBlock(node.elseBody(), builder);
        }

        builder.newLine();
    }

    private static void serialiseImport(TypeScriptImportNamedNode node, CodeBuilder builder) {
        builder.append("import ");
        builder.append("{");
        forEachInterspersed(
            node.members(),
            member -> {
                builder.append(member.exportName());
                if (!member.exportName().equals(member.importName())) {
                    builder.append(" as ");
                    builder.append(member.importName());
                }
            },
            () -> builder.append(", ")
        );
        builder.append("}");
        builder.append(" from ");
        serialiseStringLiteral(node.module(), builder);
        builder.append(";");
        builder.newLine();
    }

    private static void serialiseImportNamespace(TypeScriptImportNamespaceNode node, CodeBuilder builder) {
        builder.append("import * as ");
        builder.append(node.name());
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
            serialiseExpression(field.type(), builder, Optional.empty());
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
        serialiseExpression(node.expression(), builder, Optional.empty());
        builder.append(";");
        builder.newLine();
    }

    public static void serialiseModule(TypeScriptModuleNode node, CodeBuilder builder) {
        for (var statement : node.statements()) {
            serialiseStatement(statement, builder);
        }
    }

    private static void serialisePropertyAccess(TypeScriptPropertyAccessNode node, CodeBuilder builder) {
        serialiseExpression(node.receiver(), builder, Optional.of(node));
        builder.append(".");
        builder.append(node.propertyName());
    }

    private static void serialiseReference(TypeScriptReferenceNode node, CodeBuilder builder) {
        builder.append(node.name());
    }

    private static void serialiseReturn(TypeScriptReturnNode node, CodeBuilder builder) {
        builder.append("return ");
        serialiseExpression(node.expression(), builder, Optional.empty());
        builder.append(";");
        builder.newLine();
    }

    private static void serialiseSingleLineComment(TypeScriptSingleLineCommentNode node, CodeBuilder builder) {
        builder.append("//");
        builder.append(node.value());
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
            public Void visit(TypeScriptExportNode node) {
                serialiseExport(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptExpressionStatementNode node) {
                serialiseExpressionStatement(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptForOfNode node) {
                serialiseForOf(node, builder);
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
            public Void visit(TypeScriptImportNamedNode node) {
                serialiseImport(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptImportNamespaceNode node) {
                serialiseImportNamespace(node, builder);
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
            public Void visit(TypeScriptSingleLineCommentNode node) {
                serialiseSingleLineComment(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptSwitchNode node) {
                serialiseSwitch(node, builder);
                return null;
            }

            @Override
            public Void visit(TypeScriptTypeDeclarationNode node) {
                serialiseTypeDeclaration(node, builder);
                return null;
            }
        });
    }

    public static void serialiseStatements(
        List<TypeScriptStatementNode> nodes,
        CodeBuilder builder
    ) {
        for (var statement : nodes) {
            serialiseStatement(statement, builder);
        }
    }

    private static void serialiseStrictEquals(TypeScriptStrictEqualsNode node, CodeBuilder builder) {
        serialiseBinaryOperation("===", node, builder);
    }

    private static void serialiseStrictNotEqual(TypeScriptStrictNotEqualNode node, CodeBuilder builder) {
        serialiseBinaryOperation("!==", node, builder);
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

    private static void serialiseSubtract(TypeScriptSubtractNode node, CodeBuilder builder) {
        serialiseBinaryOperation("-", node, builder);
    }

    private static void serialiseSwitch(TypeScriptSwitchNode node, CodeBuilder builder) {
        builder.append("switch (");
        serialiseExpression(node.expression(), builder, Optional.empty());
        builder.append(") {");
        builder.newLine();
        builder.indent();

        for (var switchCase : node.cases()) {
            builder.append("case ");
            serialiseExpression(switchCase.expression(), builder, Optional.empty());
            builder.append(":");
            builder.newLine();
            builder.indent();
            for (var statement: switchCase.body()) {
                serialiseStatement(statement, builder);
            }
            builder.dedent();
        }

        builder.dedent();
        builder.append("}");
        builder.newLine();
    }

    private static void serialiseTypeDeclaration(TypeScriptTypeDeclarationNode node, CodeBuilder builder) {
        builder.append("type ");
        builder.append(node.name());
        builder.append(" = ");
        serialiseExpression(node.value(), builder, Optional.empty());
        builder.append(";");
        builder.newLine();
    }

    private static void serialiseUnion(TypeScriptUnionNode node, CodeBuilder builder) {
        forEachInterspersed(
            node.members(),
            member -> serialiseExpression(member, builder, Optional.of(node)),
            () -> builder.append(" | ")
        );
    }
}
