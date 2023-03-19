package org.zwobble.clunk.ast.untyped;

public interface UntypedExpressionNode extends UntypedNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(UntypedAddNode node);
        T visit(UntypedBoolLiteralNode node);
        T visit(UntypedCallNode node);
        T visit(UntypedCastUnsafeNode node);
        T visit(UntypedEqualsNode node);
        T visit(UntypedIndexNode node);
        T visit(UntypedInstanceOfNode node);
        T visit(UntypedIntLiteralNode node);
        T visit(UntypedListLiteralNode node);
        T visit(UntypedLogicalAndNode node);
        T visit(UntypedLogicalNotNode node);
        T visit(UntypedLogicalOrNode node);
        T visit(UntypedMapLiteralNode node);
        T visit(UntypedMemberAccessNode node);
        T visit(UntypedMemberDefinitionReferenceNode node);
        T visit(UntypedNotEqualNode node);
        T visit(UntypedReferenceNode node);
        T visit(UntypedStringLiteralNode node);
    }
}
