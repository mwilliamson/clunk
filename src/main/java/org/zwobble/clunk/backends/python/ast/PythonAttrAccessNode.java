package org.zwobble.clunk.backends.python.ast;

public record PythonAttrAccessNode(
    PythonExpressionNode receiver,
    String attrName
) implements PythonExpressionNode {
    @Override
    public PythonPrecedence precedence() {
        return PythonPrecedence.CALL;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
