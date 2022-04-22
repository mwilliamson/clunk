package org.zwobble.clunk.ast.untyped;

public interface UntypedExpressionNode extends UntypedNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(UntypedBoolLiteralNode node);
        T visit(UntypedCallNode node);
        T visit(UntypedFieldAccessNode node);
        T visit(UntypedIntLiteralNode node);
        T visit(UntypedReferenceNode node);
        T visit(UntypedStringLiteralNode node);
    }
}
