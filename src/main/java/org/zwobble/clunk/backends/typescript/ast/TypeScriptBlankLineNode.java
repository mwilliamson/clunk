package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptBlankLineNode() implements TypeScriptClassBodyDeclarationNode, TypeScriptStatementNode {
    @Override
    public <T> T accept(TypeScriptClassBodyDeclarationNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(TypeScriptStatementNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
