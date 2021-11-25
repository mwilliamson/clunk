package org.zwobble.clunk.backends.python.ast;

public record PythonAssignmentNode(
    String name,
    PythonExpressionNode type
) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
