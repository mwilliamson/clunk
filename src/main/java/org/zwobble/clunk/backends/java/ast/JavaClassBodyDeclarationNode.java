package org.zwobble.clunk.backends.java.ast;

public interface JavaClassBodyDeclarationNode extends JavaNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaMethodDeclarationNode node);
    }
}
