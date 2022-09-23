package org.zwobble.clunk.backends.java.ast;

public record JavaWildcardTypeNode() implements JavaTypeExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
