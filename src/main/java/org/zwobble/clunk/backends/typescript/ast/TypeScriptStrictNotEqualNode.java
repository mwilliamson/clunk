package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptStrictNotEqualNode(
    TypeScriptExpressionNode left,
    TypeScriptExpressionNode right
) implements TypeScriptBinaryOperationNode, TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.EQUALITY;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
