package org.zwobble.clunk.backends.java.ast;

public interface JavaTypeDeclarationNode extends JavaNode {
    String name();
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaClassDeclarationNode node);
        T visit(JavaInterfaceDeclarationNode node);
        T visit(JavaRecordDeclarationNode node);
    }
}
