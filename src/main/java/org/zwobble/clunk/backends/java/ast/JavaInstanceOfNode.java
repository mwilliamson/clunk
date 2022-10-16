package org.zwobble.clunk.backends.java.ast;

public record JavaInstanceOfNode(
    JavaExpressionNode expression,
    JavaTypeExpressionNode typeExpressionNode
) implements JavaExpressionNode {
    @Override
    public JavaPrecedence precedence() {
        return JavaPrecedence.RELATIONAL;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
