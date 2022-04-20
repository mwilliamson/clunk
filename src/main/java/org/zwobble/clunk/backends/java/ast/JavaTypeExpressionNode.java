package org.zwobble.clunk.backends.java.ast;

public interface JavaTypeExpressionNode extends JavaNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaTypeVariableReferenceNode node);
    }
}
