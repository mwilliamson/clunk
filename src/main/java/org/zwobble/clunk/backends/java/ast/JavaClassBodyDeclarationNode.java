package org.zwobble.clunk.backends.java.ast;

public interface JavaClassBodyDeclarationNode extends JavaNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaBlankLineNode node);
        T visit(JavaClassDeclarationNode node);
        T visit(JavaMethodDeclarationNode node);
        T visit(JavaSingleLineCommentNode node);
    }
}
