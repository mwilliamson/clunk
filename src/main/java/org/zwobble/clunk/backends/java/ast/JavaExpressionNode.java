package org.zwobble.clunk.backends.java.ast;

public interface JavaExpressionNode extends JavaNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaBoolLiteralNode node);
        T visit(JavaCallNode node);
        T visit(JavaCallNewNode node);
        T visit(JavaIntLiteralNode node);
        T visit(JavaMemberAccessNode node);
        T visit(JavaReferenceNode node);
        T visit(JavaStringLiteralNode node);
    }
}
