package org.zwobble.clunk.backends.java.ast;

public record JavaLogicalOrNode(
    JavaExpressionNode left,
    JavaExpressionNode right
) implements JavaBinaryOperationNode, JavaExpressionNode {
    @Override
    public JavaPrecedence precedence() {
        return JavaPrecedence.LOGICAL_OR;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
