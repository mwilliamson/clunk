package org.zwobble.clunk.backends.java.ast;

public record JavaLogicalAndNode(
    JavaExpressionNode left,
    JavaExpressionNode right
) implements JavaBinaryOperationNode, JavaExpressionNode {
    @Override
    public JavaPrecedence precedence() {
        return JavaPrecedence.LOGICAL_AND;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
