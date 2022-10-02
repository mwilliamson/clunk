package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptCastNode(
    TypeScriptExpressionNode expression,
    TypeScriptExpressionNode type
) implements TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        // The precedence of "as" in TypeScript is unclear, so we always wrap
        // in parens and treat as a primary expression.
        return TypeScriptPrecedence.PRIMARY;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
