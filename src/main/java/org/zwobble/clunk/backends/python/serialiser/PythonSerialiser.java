package org.zwobble.clunk.backends.python.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.python.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.zwobble.clunk.util.Iterables.forEachInterspersed;

public class PythonSerialiser {
    private static void serialiseAdd(PythonAddNode node, CodeBuilder builder) {
        serialiseBinaryOperation("+", node, builder);
    }

    public static void serialiseArgs(PythonArgsNode node, CodeBuilder builder) {
        forEachInterspersed(
            node.positional(),
            arg -> serialiseExpression(arg, builder, Optional.empty()),
            () -> builder.append(", ")
        );

        if (node.positional().size() > 0 && node.keyword().size() > 0) {
            builder.append(", ");
        }

        forEachInterspersed(
            node.keyword(),
            kwarg -> {
                builder.append(kwarg.name());
                builder.append("=");
                serialiseExpression(kwarg.expression(), builder, Optional.empty());
            },
            () -> builder.append(", ")
        );
    }

    private static void serialiseAssert(PythonAssertNode node, CodeBuilder builder) {
        builder.append("assert ");
        serialiseExpression(node.expression(), builder, Optional.empty());
        builder.newLine();
    }

    private static void serialiseAssignment(PythonAssignmentNode node, CodeBuilder builder) {
        builder.append(node.name());

        if (node.type().isPresent()) {
            builder.append(": ");
            serialiseExpression(node.type().get(), builder, Optional.empty());
        }

        if (node.expression().isPresent()) {
            builder.append(" = ");
            serialiseExpression(node.expression().get(), builder, Optional.empty());
        }

        builder.newLine();
    }

    private static void serialiseAttrAccess(PythonAttrAccessNode node, CodeBuilder builder) {
        serialiseExpression(node.receiver(), builder, Optional.of(node));
        builder.append(".");
        builder.append(node.attrName());
    }

    private static void serialiseBinaryOperation(String operator, PythonBinaryOperationNode node, CodeBuilder builder) {
        serialiseExpression(node.left(), builder, Optional.of(node));
        builder.append(" ");
        builder.append(operator);
        builder.append(" ");
        serialiseExpression(node.right(), builder, Optional.of(node));
    }

    private static void serialiseBlankLine(PythonBlankLineNode node, CodeBuilder builder) {
        builder.newLine();
    }

    private static void serialiseBlock(List<? extends PythonStatementNode> statements, CodeBuilder builder) {
        builder.indent();
        if (statements.isEmpty()) {
            builder.append("pass");
            builder.newLine();
        } else {
            serialiseStatements(statements, builder);
        }
        builder.dedent();
    }

    private static void serialiseBoolAnd(PythonBoolAndNode node, CodeBuilder builder) {
        serialiseBinaryOperation("and", node, builder);
    }

    private static void serialiseBoolLiteral(PythonBoolLiteralNode node, CodeBuilder builder) {
        builder.append(node.value() ? "True" : "False");
    }

    private static void serialiseBoolNot(PythonBoolNotNode node, CodeBuilder builder) {
        builder.append("not ");
        serialiseExpression(node.operand(), builder, Optional.of(node));
    }

    private static void serialiseBoolOr(PythonBoolOrNode node, CodeBuilder builder) {
        serialiseBinaryOperation("or", node, builder);
    }

    private static void serialiseCall(PythonCallNode node, CodeBuilder builder) {
        serialiseExpression(node.receiver(), builder, Optional.of(node));
        builder.append("(");
        serialiseArgs(node.args(), builder);
        builder.append(")");
    }

    private static void serialiseClassDeclaration(PythonClassDeclarationNode node, CodeBuilder builder) {
        serialiseDecorators(node.decorators(), builder);
        builder.append("class ");
        builder.append(node.name());
        if (!node.baseClasses().isEmpty()) {
            builder.append("(");
            forEachInterspersed(
                node.baseClasses(),
                baseClass -> serialiseExpression(baseClass, builder, Optional.empty()),
                () -> builder.append(", ")
            );
            builder.append(")");
        }
        builder.append(":");
        builder.newLine();
        serialiseBlock(node.statements(), builder);
    }

    private static void serialiseComprehensionForClause(
        PythonComprehensionForClauseNode forClause,
        CodeBuilder builder
    ) {
        builder.append("for ");
        builder.append(forClause.target());
        builder.append(" in ");
        serialiseExpressionTopLevel(forClause.iterable(), builder);
    }

    private static void serialiseDecorators(List<? extends PythonExpressionNode> decorators, CodeBuilder builder) {
        for (var decorator : decorators) {
            builder.append("@");
            serialiseExpression(decorator, builder, Optional.empty());
            builder.newLine();
        }
    }

    private static void serialiseDict(PythonDictNode node, CodeBuilder builder) {
        builder.append("{");
        forEachInterspersed(
            node.items(),
            item -> {
                serialiseExpressionTopLevel(item.key(), builder);
                builder.append(": ");
                serialiseExpressionTopLevel(item.value(), builder);
            },
            () -> builder.append(", ")
        );
        builder.append("}");
    }

    private static void serialiseEquals(PythonEqualsNode node, CodeBuilder builder) {
        serialiseBinaryOperation("==", node, builder);
    }

    private static void serialiseExpressionTopLevel(PythonExpressionNode node, CodeBuilder builder) {
        serialiseExpression(node, builder, Optional.empty());
    }

    public static void serialiseExpression(PythonExpressionNode node, CodeBuilder builder, Optional<PythonExpressionNode> parent) {
        var parenthesize = parent.isPresent() && node.precedence().ordinal() < parent.get().precedence().ordinal();
        if (parenthesize) {
            builder.append("(");
        }

        node.accept(new PythonExpressionNode.Visitor<Void>() {
            @Override
            public Void visit(PythonAddNode node) {
                serialiseAdd(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonAttrAccessNode node) {
                serialiseAttrAccess(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonBoolAndNode node) {
                serialiseBoolAnd(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonBoolLiteralNode node) {
                serialiseBoolLiteral(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonBoolNotNode node) {
                serialiseBoolNot(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonBoolOrNode node) {
                serialiseBoolOr(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonCallNode node) {
                serialiseCall(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonDictNode node) {
                serialiseDict(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonEqualsNode node) {
                serialiseEquals(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonInNode node) {
                serialiseIn(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonIntLiteralNode node) {
                serialiseIntLiteral(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonListNode node) {
                serialiseList(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonListComprehensionNode node) {
                serialiseListComprehension(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonNotEqualNode node) {
                serialiseNotEqual(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonReferenceNode node) {
                serialiseReference(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonStringLiteralNode node) {
                serialiseStringLiteral(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonSubscriptionNode node) {
                serialiseSubscription(node, builder);
                return null;
            }
        });

        if (parenthesize) {
            builder.append(")");
        }
    }

    private static void serialiseExpressionStatement(
        PythonExpressionStatementNode node,
        CodeBuilder builder
    ) {
        serialiseExpression(node.expression(), builder, Optional.empty());
        builder.newLine();
    }

    private static void serialiseForEach(PythonForEachNode node, CodeBuilder builder) {
        builder.append("for ");
        builder.append(node.targetName());
        builder.append(" in ");
        serialiseExpressionTopLevel(node.iterable(), builder);
        builder.append(":");
        builder.newLine();
        serialiseBlock(node.body(), builder);
    }

    private static void serialiseFunction(PythonFunctionNode node, CodeBuilder builder) {
        serialiseDecorators(node.decorators(), builder);
        builder.append("def ");
        builder.append(node.name());
        builder.append("(");

        var params = new ArrayList<String>();
        if (node.params().hasSelf()) {
            params.add("self");
        }
        params.addAll(node.params().positional());
        if (node.params().positional().size() > 0) {
            params.add("/");
        }
        if (node.params().keyword().size() > 0) {
            params.add("*");
        }
        params.addAll(node.params().keyword());
        builder.append(String.join(", ", params));

        builder.append("):");
        builder.newLine();
        serialiseBlock(node.body(), builder);
    }

    private static void serialiseIfStatement(PythonIfStatementNode node, CodeBuilder builder) {
        var firstConditionalBranch = node.conditionalBranches().get(0);

        builder.append("if ");
        serialiseExpression(firstConditionalBranch.condition(), builder, Optional.empty());
        builder.append(":");
        builder.newLine();
        serialiseBlock(firstConditionalBranch.body(), builder);

        node.conditionalBranches().stream().skip(1).forEachOrdered(conditionalBranch -> {
            builder.append("elif ");
            serialiseExpression(conditionalBranch.condition(), builder, Optional.empty());
            builder.append(":");
            builder.newLine();
            serialiseBlock(conditionalBranch.body(), builder);
        });

        if (node.elseBody().size() > 0) {
            builder.append("else:");
            builder.newLine();
            serialiseBlock(node.elseBody(), builder);
        }
    }

    private static void serialiseImport(PythonImportNode node, CodeBuilder builder) {
        builder.append("import ");
        serialiseModuleName(node.moduleName(), builder);
        builder.newLine();
    }

    private static void serialiseImportFrom(PythonImportFromNode node, CodeBuilder builder) {
        builder.append("from ");
        serialiseModuleName(node.moduleName(), builder);
        builder.append(" import ");
        forEachInterspersed(
            node.names(),
            name -> builder.append(name),
            () -> builder.append(", ")
        );
        builder.newLine();
    }

    private static void serialiseIn(PythonInNode node, CodeBuilder builder) {
        serialiseBinaryOperation("in", node, builder);
    }

    private static void serialiseIntLiteral(PythonIntLiteralNode node, CodeBuilder builder) {
        builder.append(node.value().toString());
    }

    private static void serialiseList(PythonListNode node, CodeBuilder builder) {
        builder.append("[");
        forEachInterspersed(
            node.elements(),
            element -> serialiseExpression(element, builder, Optional.empty()),
            () -> builder.append(", ")
        );
        builder.append("]");
    }

    private static void serialiseListComprehension(
        PythonListComprehensionNode node,
        CodeBuilder builder
    ) {
        builder.append("[");
        serialiseExpressionTopLevel(node.element(), builder);

        for (var forClause : node.forClauses()) {
            builder.append(" ");
            serialiseComprehensionForClause(forClause, builder);
        }

        builder.append("]");
    }

    public static void serialiseModule(PythonModuleNode node, CodeBuilder builder) {
        for (var statement : node.statements()) {
            serialiseStatement(statement, builder);
        }
    }

    private static void serialiseModuleName(List<String> moduleName, CodeBuilder builder) {
        builder.append(String.join(".", moduleName));
    }

    private static void serialiseNotEqual(PythonNotEqualNode node, CodeBuilder builder) {
        serialiseBinaryOperation("!=", node, builder);
    }

    private static void serialiseReference(PythonReferenceNode node, CodeBuilder builder) {
        builder.append(node.name());
    }

    private static void serialiseReturn(PythonReturnNode node, CodeBuilder builder) {
        builder.append("return ");
        serialiseExpression(node.expression(), builder, Optional.empty());
        builder.newLine();
    }

    private static void serialiseSingleLineComment(PythonSingleLineCommentNode node, CodeBuilder builder) {
        builder.append("#");
        builder.append(node.value());
        builder.newLine();
    }

    public static void serialiseStatement(PythonStatementNode node, CodeBuilder builder) {
        node.accept(new PythonStatementNode.Visitor<Void>() {
            @Override
            public Void visit(PythonAssertNode node) {
                serialiseAssert(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonAssignmentNode node) {
                serialiseAssignment(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonBlankLineNode node) {
                serialiseBlankLine(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonClassDeclarationNode node) {
                serialiseClassDeclaration(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonExpressionStatementNode node) {
                serialiseExpressionStatement(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonForEachNode node) {
                serialiseForEach(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonFunctionNode node) {
                serialiseFunction(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonIfStatementNode node) {
                serialiseIfStatement(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonImportNode node) {
                serialiseImport(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonImportFromNode node) {
                serialiseImportFrom(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonReturnNode node) {
                serialiseReturn(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonSingleLineCommentNode node) {
                serialiseSingleLineComment(node, builder);
                return null;
            }
        });
    }

    public static void serialiseStatements(List<? extends PythonStatementNode> statements, CodeBuilder builder) {
        for (var statement : statements) {
            serialiseStatement(statement, builder);
        }
    }

    private static void serialiseStringLiteral(PythonStringLiteralNode node, CodeBuilder builder) {
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

    private static void serialiseSubscription(PythonSubscriptionNode node, CodeBuilder builder) {
        serialiseExpression(node.receiver(), builder, Optional.of(node));
        builder.append("[");
        forEachInterspersed(
            node.args(),
            arg -> serialiseExpression(arg, builder, Optional.empty()),
            () -> builder.append(", ")
        );
        builder.append("]");
    }
}
