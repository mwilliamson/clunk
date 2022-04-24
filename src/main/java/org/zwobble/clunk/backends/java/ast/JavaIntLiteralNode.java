package org.zwobble.clunk.backends.java.ast;

public record JavaIntLiteralNode(int value) implements JavaExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}