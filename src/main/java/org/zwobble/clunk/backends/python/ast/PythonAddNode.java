package org.zwobble.clunk.backends.python.ast;

public record PythonAddNode(
    PythonExpressionNode left,
    PythonExpressionNode right
) implements PythonExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
