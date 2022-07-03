package org.zwobble.clunk.backends.java.ast;

public interface JavaTypeExpressionNode extends JavaNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaFullyQualifiedTypeReferenceNode node);
        T visit(JavaParameterizedType node);
        T visit(JavaTypeVariableReferenceNode node);
    }
}
