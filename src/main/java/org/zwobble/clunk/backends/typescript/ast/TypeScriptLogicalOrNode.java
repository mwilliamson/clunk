package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptLogicalOrNode(
    TypeScriptExpressionNode left,
    TypeScriptExpressionNode right
) implements TypeScriptBinaryOperationNode, TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.LOGICAL_OR;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
