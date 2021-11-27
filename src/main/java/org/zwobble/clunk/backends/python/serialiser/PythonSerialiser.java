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

    private static void serialiseAttrAccess(PythonAttrAccessNode node, CodeBuilder builder) {
        builder.append("(");
        serialiseExpression(node.receiver(), builder);
        builder.append(").");
        builder.append(node.attrName());
    }

    private static void serialiseBlock(List<? extends PythonStatementNode> statements, CodeBuilder builder) {
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

    private static void serialiseClassDeclaration(PythonClassDeclarationNode node, CodeBuilder builder) {
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

    public static void serialiseExpression(PythonExpressionNode node, CodeBuilder builder) {
        node.accept(new PythonExpressionNode.Visitor<Void>() {
            @Override
            public Void visit(PythonAttrAccessNode node) {
                serialiseAttrAccess(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonReferenceNode node) {
                serialiseReference(node, builder);
                return null;
            }
        });
    }

    private static void serialiseImport(PythonImportNode node, CodeBuilder builder) {
        builder.append("import ");
        builder.append(node.moduleName());
        builder.newLine();
    }

    private static void serialiseReference(PythonReferenceNode node, CodeBuilder builder) {
        builder.append(node.name());
    }

    public static void serialiseStatement(PythonStatementNode node, CodeBuilder builder) {
        node.accept(new PythonStatementNode.Visitor<Void>() {
            @Override
            public Void visit(PythonAssignmentNode node) {
                serialiseAssignment(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonClassDeclarationNode node) {
                serialiseClassDeclaration(node, builder);
                return null;
            }

            @Override
            public Void visit(PythonImportNode node) {
                serialiseImport(node, builder);
                return null;
            }
        });
    }
}
