package org.zwobble.clunk.backends.java.ast;

public record JavaAddNode(
    JavaExpressionNode left,
    JavaExpressionNode right
) implements JavaBinaryOperationNode, JavaExpressionNode {
    @Override
    public JavaPrecedence precedence() {
        return JavaPrecedence.ADDITIVE;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
