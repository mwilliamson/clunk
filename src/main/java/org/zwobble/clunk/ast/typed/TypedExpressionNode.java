package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.types.Type;

public interface TypedExpressionNode extends TypedNode {
    Type type();
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(TypedBoolLiteralNode node);
        T visit(TypedCallNode node);
        T visit(TypedIndexNode node);
        T visit(TypedIntAddNode node);
        T visit(TypedIntLiteralNode node);
        T visit(TypedMemberAccessNode node);
        T visit(TypedMemberReferenceNode node);
        T visit(TypedReferenceNode node);
        T visit(TypedStringLiteralNode node);
    }
}
