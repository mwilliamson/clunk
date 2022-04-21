package org.zwobble.clunk.backends.java.ast;

public interface JavaStatementNode extends JavaNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaExpressionStatementNode node);
        T visit(JavaReturnNode node);
        T visit(JavaVariableDeclarationNode node);
    }
}
