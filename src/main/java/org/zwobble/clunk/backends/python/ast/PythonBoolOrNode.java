package org.zwobble.clunk.backends.python.ast;

public record PythonBoolOrNode(
    PythonExpressionNode left,
    PythonExpressionNode right
) implements PythonBinaryOperationNode, PythonExpressionNode {
    @Override
    public PythonPrecedence precedence() {
        return PythonPrecedence.BOOLEAN_OR;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
