package org.zwobble.clunk.backends.java.ast;

public record JavaExpressionStatementNode(JavaExpressionNode expression) implements JavaStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
