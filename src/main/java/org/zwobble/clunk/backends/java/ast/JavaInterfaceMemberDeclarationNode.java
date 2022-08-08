package org.zwobble.clunk.backends.java.ast;

public interface JavaInterfaceMemberDeclarationNode extends JavaNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaInterfaceMethodDeclarationNode node);
    }
}
