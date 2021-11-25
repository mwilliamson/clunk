package org.zwobble.clunk.backends.python.serialiser;

import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.python.ast.*;

import java.util.List;

public class PythonSerialiser {
    private static void serialiseAssignment(PythonAssignmentNode node, CodeBuilder builder) {
        builder.append(node.name());
        builder.append(": ");
        serialiseExpression(node.type(), builder);
        builder.newLine();
    }

    private static void serialiseBlock(List<PythonStatementNode> statements, CodeBuilder builder) {
        builder.indent();
        if (statements.isEmpty()) {
            builder.append("pass");
            builder.newLine();
        } else {
            for (var statement : statements) {
                serialiseStatement(statement, builder);
            }
        }
        builder.dedent();
    }

    public static void serialiseClassDeclaration(PythonClassDeclarationNode node, CodeBuilder builder) {
        for (var decorator : node.decorators()) {
            builder.append("@");
            serialiseExpression(decorator, builder);
            builder.newLine();
        }
        builder.append("class ");
        builder.append(node.name());
        builder.append(":");
        builder.newLine();
        serialiseBlock(node.statements(), builder);
    }

    private static void serialiseExpression(PythonExpressionNode node, CodeBuilder builder) {
        node.accept(new PythonExpressionNode.Visitor<Void>() {
            @Override
            public Void visit(PythonReferenceNode node) {
                serialiseReference(node, builder);
                return null;
            }
        });
    }

    public static void serialiseReference(PythonReferenceNode node, CodeBuilder builder) {
        builder.append(node.name());
    }

    public static void serialiseStatement(PythonStatementNode node, CodeBuilder builder) {
        node.accept(new PythonStatementNode.Visitor<Void>() {
            @Override
            public Void visit(PythonAssignmentNode node) {
                serialiseAssignment(node, builder);
                return null;
            }
        });
    }
}
