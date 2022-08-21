package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptLogicalAndNode(
    TypeScriptExpressionNode left,
    TypeScriptExpressionNode right
) implements TypeScriptBinaryOperationNode, TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.LOGICAL_AND;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
