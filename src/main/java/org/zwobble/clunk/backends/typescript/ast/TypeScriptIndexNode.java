package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptIndexNode(
    TypeScriptExpressionNode receiver,
    TypeScriptExpressionNode index
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
