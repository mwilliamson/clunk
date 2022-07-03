package org.zwobble.clunk.backends.python.ast;

public interface PythonExpressionNode extends PythonNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(PythonAttrAccessNode node);
        T visit(PythonBoolLiteralNode node);
        T visit(PythonCallNode node);
        T visit(PythonIntLiteralNode node);
        T visit(PythonReferenceNode node);
        T visit(PythonStringLiteralNode node);
        T visit(PythonSubscriptionNode node);
    }
}
