package org.zwobble.clunk.backends.java.ast;

public record JavaMemberAccessNode(
    JavaExpressionNode receiver,
    String memberName
) implements JavaExpressionNode {
    @Override
    public JavaPrecedence precedence() {
        return JavaPrecedence.CALL;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
