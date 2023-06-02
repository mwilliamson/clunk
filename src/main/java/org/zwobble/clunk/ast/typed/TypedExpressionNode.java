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
        T visit(TypedCastUnsafeNode node);
        T visit(TypedIndexNode node);
        T visit(TypedInstanceOfNode node);
        T visit(TypedIntAddNode node);
        T visit(TypedIntEqualsNode node);
        T visit(TypedIntLiteralNode node);
        T visit(TypedIntNotEqualNode node);
        T visit(TypedListComprehensionNode node);
        T visit(TypedListLiteralNode node);
        T visit(TypedLocalReferenceNode node);
        T visit(TypedLogicalAndNode node);
        T visit(TypedLogicalNotNode node);
        T visit(TypedLogicalOrNode node);
        T visit(TypedMapLiteralNode node);
        T visit(TypedMemberAccessNode node);
        T visit(TypedMemberDefinitionReferenceNode node);
        T visit(TypedMemberReferenceNode node);
        T visit(TypedStaticMethodToFunctionNode node);
        T visit(TypedStringEqualsNode node);
        T visit(TypedStringLiteralNode node);
        T visit(TypedStringNotEqualNode node);
        T visit(TypedStructuredEqualsNode node);
        T visit(TypedStructuredNotEqualNode node);
    }
}
