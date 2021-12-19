package org.zwobble.clunk.backends.java.ast;

public record JavaVariableDeclarationNode(String name, JavaExpressionNode expression) implements JavaStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
