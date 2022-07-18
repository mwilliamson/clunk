package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptPropertyAccessNode(
    TypeScriptExpressionNode receiver,
    String propertyName
) implements TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.CALL;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
