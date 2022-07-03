package org.zwobble.clunk.backends.python.ast;

public record PythonSubscriptionNode(
    PythonExpressionNode receiver,
    PythonExpressionNode arg
) implements PythonExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
