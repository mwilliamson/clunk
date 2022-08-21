package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptSingleLineCommentNode(String value) implements TypeScriptStatementNode, TypeScriptClassBodyDeclarationNode {
    @Override
    public <T> T accept(TypeScriptClassBodyDeclarationNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(TypeScriptStatementNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
