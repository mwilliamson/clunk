package org.zwobble.clunk.backends.python.serialiser;

import org.zwobble.clunk.backends.python.ast.*;

public class PythonSerialiser {
    private static void serialiseAssignment(PythonAssignmentNode node, StringBuilder builder) {
        builder.append(node.name());
        builder.append(": ");
        serialiseExpression(node.type(), builder);
    }

    public static void serialiseClassDeclaration(PythonClassDeclarationNode node, StringBuilder builder) {
        for (var decorator : node.decorators()) {
            builder.append("@");
            serialiseExpression(decorator, builder);
            builder.append("\n");
        }
        builder.append("class ");
        builder.append(node.name());
        builder.append(":\n");
        builder.append("    pass");
    }

    private static void serialiseExpression(PythonExpressionNode node, StringBuilder builder) {
        node.accept(new PythonExpressionNode.Visitor<Void>() {
            @Override
            public Void visit(PythonReferenceNode node) {
                serialiseReference(node, builder);
                return null;
            }
        });
    }

    public static void serialiseReference(PythonReferenceNode node, StringBuilder builder) {
        builder.append(node.name());
    }

    public static void serialiseStatement(PythonAssignmentNode node, StringBuilder builder) {
        node.accept(new PythonStatementNode.Visitor<Void>() {
            @Override
            public Void visit(PythonAssignmentNode node) {
                serialiseAssignment(node, builder);
                return null;
            }
        });
    }
}
