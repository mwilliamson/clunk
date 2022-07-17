package org.zwobble.clunk.backends.java.ast;

public interface JavaImportNode extends JavaNode {
    interface Visitor<T> {
        T visit(JavaImportStaticNode node);
        T visit(JavaImportTypeNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
