package org.zwobble.clunk.backends.java.ast;

public record JavaExtendsTypeNode(
    JavaWildcardTypeNode arg,
    JavaTypeExpressionNode extends_
) implements JavaTypeExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
