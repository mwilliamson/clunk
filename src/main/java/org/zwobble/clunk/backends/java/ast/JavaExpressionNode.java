package org.zwobble.clunk.backends.java.ast;

public interface JavaExpressionNode extends JavaNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaBoolLiteralNode node);
        T visit(JavaStringLiteralNode node);
    }
}
