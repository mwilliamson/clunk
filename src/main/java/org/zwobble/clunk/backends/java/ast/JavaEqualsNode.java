package org.zwobble.clunk.backends.java.ast;

public record JavaEqualsNode(
    JavaExpressionNode left,
    JavaExpressionNode right
) implements JavaBinaryOperationNode, JavaExpressionNode {
    @Override
    public JavaPrecedence precedence() {
        return JavaPrecedence.EQUALITY;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
