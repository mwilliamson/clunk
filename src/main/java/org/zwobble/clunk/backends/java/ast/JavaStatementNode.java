package org.zwobble.clunk.backends.java.ast;

public interface JavaStatementNode extends JavaNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaBlankLineNode node);
        T visit(JavaExpressionStatementNode node);
        T visit(JavaIfStatementNode node);
        T visit(JavaReturnNode node);
        T visit(JavaSingleLineCommentNode node);
        T visit(JavaVariableDeclarationNode node);
    }
}
