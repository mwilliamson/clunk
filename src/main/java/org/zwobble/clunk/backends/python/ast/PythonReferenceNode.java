package org.zwobble.clunk.backends.python.ast;

public record PythonReferenceNode(String name) implements PythonExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
