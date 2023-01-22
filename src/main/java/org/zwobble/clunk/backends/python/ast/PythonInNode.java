package org.zwobble.clunk.backends.python.ast;

public record PythonInNode(
    PythonExpressionNode left,
    PythonExpressionNode right
) implements PythonBinaryOperationNode {
    @Override
    public PythonPrecedence precedence() {
        return PythonPrecedence.COMPARISON;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
