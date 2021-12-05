package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptBoolLiteralNode(boolean value) implements TypeScriptExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
