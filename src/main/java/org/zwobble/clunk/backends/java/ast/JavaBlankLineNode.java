package org.zwobble.clunk.backends.java.ast;

public record JavaBlankLineNode() implements JavaClassBodyDeclarationNode, JavaStatementNode {
    @Override
    public <T> T accept(JavaClassBodyDeclarationNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(JavaStatementNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
