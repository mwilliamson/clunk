package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptNonNullAssertionNode(
    TypeScriptExpressionNode operand
) implements TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.POSTFIX;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
