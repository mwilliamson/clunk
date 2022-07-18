package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptBlankLineNode() implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
