package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptLogicalNotNode(
    TypeScriptExpressionNode operand
) implements TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.PREFIX;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
