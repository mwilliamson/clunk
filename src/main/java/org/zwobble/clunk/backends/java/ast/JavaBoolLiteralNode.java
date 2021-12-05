package org.zwobble.clunk.backends.java.ast;

public record JavaBoolLiteralNode(boolean value) implements JavaExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
