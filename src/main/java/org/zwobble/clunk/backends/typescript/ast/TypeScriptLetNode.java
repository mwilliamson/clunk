package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptLetNode(
    String name,
    TypeScriptExpressionNode expression
) implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
