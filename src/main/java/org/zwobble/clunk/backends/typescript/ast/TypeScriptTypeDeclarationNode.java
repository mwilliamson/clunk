package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptTypeDeclarationNode(
    String name,
    TypeScriptExpressionNode value
) implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
