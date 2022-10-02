package org.zwobble.clunk.backends.java.ast;

public record JavaCastNode(
    JavaTypeExpressionNode type,
    JavaExpressionNode expression
) implements JavaExpressionNode {
    @Override
    public JavaPrecedence precedence() {
        return JavaPrecedence.UNARY;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
