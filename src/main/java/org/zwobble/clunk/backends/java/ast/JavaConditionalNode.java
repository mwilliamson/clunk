package org.zwobble.clunk.backends.java.ast;

public record JavaConditionalNode(
    JavaExpressionNode condition,
    JavaExpressionNode trueExpression,
    JavaExpressionNode falseExpression
) implements JavaExpressionNode {
    @Override
    public JavaPrecedence precedence() {
        return JavaPrecedence.TERNARY;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
