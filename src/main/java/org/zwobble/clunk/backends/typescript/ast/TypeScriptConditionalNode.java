package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptConditionalNode(
    TypeScriptExpressionNode condition,
    TypeScriptExpressionNode trueExpression,
    TypeScriptExpressionNode falseExpression
) implements TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.ASSIGNMENT;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
