package org.zwobble.clunk.backends.java.ast;

public record JavaSingleLineCommentNode(String value) implements JavaStatementNode, JavaClassBodyDeclarationNode {
    @Override
    public <T> T accept(JavaClassBodyDeclarationNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(JavaStatementNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
