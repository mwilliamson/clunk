package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.types.Type;

public interface TypedExpressionNode extends TypedNode {
    Type type();
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(TypedBoolLiteralNode node);
        T visit(TypedCallConstructorNode node);
        T visit(TypedCallMethodNode node);
        T visit(TypedCallStaticFunctionNode node);
        T visit(TypedIndexNode node);
        T visit(TypedIntAddNode node);
        T visit(TypedIntLiteralNode node);
        T visit(TypedLocalReferenceNode node);
        T visit(TypedLogicalAndNode node);
        T visit(TypedLogicalNotNode node);
        T visit(TypedLogicalOrNode node);
        T visit(TypedMemberAccessNode node);
        T visit(TypedMemberReferenceNode node);
        T visit(TypedStringEqualsNode node);
        T visit(TypedStringLiteralNode node);
    }
}
