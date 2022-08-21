package org.zwobble.clunk.backends.python.ast;

public record PythonEqualsNode(
    PythonExpressionNode left,
    PythonExpressionNode right
) implements PythonBinaryOperationNode, PythonExpressionNode {
    @Override
    public PythonPrecedence precedence() {
        return PythonPrecedence.COMPARISON;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
