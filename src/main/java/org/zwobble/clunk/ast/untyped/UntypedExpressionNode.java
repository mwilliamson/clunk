package org.zwobble.clunk.ast.untyped;

public interface UntypedExpressionNode extends UntypedNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(UntypedStringLiteralNode node);
    }
}