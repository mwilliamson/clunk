package org.zwobble.clunk.backends.java.ast;

public record JavaReferenceNode(String name) implements JavaExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
