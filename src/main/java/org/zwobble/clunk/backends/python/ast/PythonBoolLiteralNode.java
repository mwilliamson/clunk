package org.zwobble.clunk.backends.python.ast;

public record PythonBoolLiteralNode(boolean value) implements PythonExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
