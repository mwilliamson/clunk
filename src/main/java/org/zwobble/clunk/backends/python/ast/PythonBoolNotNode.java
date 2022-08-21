package org.zwobble.clunk.backends.python.ast;

public record PythonBoolNotNode(
    PythonExpressionNode operand
) implements PythonExpressionNode {
    @Override
    public PythonPrecedence precedence() {
        return PythonPrecedence.BOOLEAN_NOT;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
