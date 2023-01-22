package org.zwobble.clunk.backends.python.ast;

public interface PythonExpressionNode extends PythonNode {
    PythonPrecedence precedence();

    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(PythonAddNode node);
        T visit(PythonAttrAccessNode node);
        T visit(PythonBoolAndNode node);
        T visit(PythonBoolLiteralNode node);
        T visit(PythonBoolNotNode node);
        T visit(PythonBoolOrNode node);
        T visit(PythonCallNode node);
        T visit(PythonDictNode node);
        T visit(PythonEqualsNode node);
        T visit(PythonInNode node);
        T visit(PythonIntLiteralNode node);
        T visit(PythonListNode node);
        T visit(PythonListComprehensionNode node);
        T visit(PythonNotEqualNode node);
        T visit(PythonReferenceNode node);
        T visit(PythonStringLiteralNode node);
        T visit(PythonSubscriptionNode node);
    }
}
