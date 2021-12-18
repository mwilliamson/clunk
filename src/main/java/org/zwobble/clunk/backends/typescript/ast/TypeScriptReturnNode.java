package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptReturnNode(TypeScriptExpressionNode expression) implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
