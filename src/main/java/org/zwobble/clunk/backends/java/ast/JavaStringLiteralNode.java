package org.zwobble.clunk.backends.java.ast;

public record JavaStringLiteralNode(String value) implements JavaExpressionNode {
    @Override
    public JavaPrecedence precedence() {
        return JavaPrecedence.PRIMARY;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
