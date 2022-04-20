package org.zwobble.clunk.backends.java.ast;

public record JavaFullyQualifiedTypeReferenceNode(
    String packageName,
    String typeName
) implements JavaTypeExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
