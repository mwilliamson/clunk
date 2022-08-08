package org.zwobble.clunk.backends.java.ast;

public interface JavaInterfaceMemberDeclarationNode extends JavaNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaInterfaceDeclarationNode node);
        T visit(JavaInterfaceMethodDeclarationNode node);
    }
}
