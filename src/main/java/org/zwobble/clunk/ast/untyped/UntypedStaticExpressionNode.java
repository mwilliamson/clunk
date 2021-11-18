package org.zwobble.clunk.ast.untyped;

public interface UntypedStaticExpressionNode extends UntypedNode {
    interface Visitor<T> {
        T visit(UntypedStaticReferenceNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
