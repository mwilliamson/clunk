package org.zwobble.clunk.backends.java.ast;

public interface JavaTypeDeclarationNode extends JavaNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaClassDeclarationNode node);
        T visit(JavaRecordDeclarationNode node);
    }
}
