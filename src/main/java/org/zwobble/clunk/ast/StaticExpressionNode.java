package org.zwobble.clunk.ast;

public interface StaticExpressionNode extends Node {
    interface Visitor<T> {
        T visit(StaticReferenceNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
