package org.zwobble.clunk.backends.python.ast;

public record PythonReturnNode(PythonExpressionNode expression) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
