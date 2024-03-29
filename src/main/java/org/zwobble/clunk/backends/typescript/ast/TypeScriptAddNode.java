package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptAddNode(
    TypeScriptExpressionNode left,
    TypeScriptExpressionNode right
) implements TypeScriptBinaryOperationNode, TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.ADDITION;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
