package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptReferenceNode(String name) implements TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.PRIMARY;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
