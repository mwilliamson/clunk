package org.zwobble.clunk.backends.typescript.ast;

public interface TypeScriptExpressionNode extends TypeScriptNode {
    TypeScriptPrecedence precedence();

    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(TypeScriptAddNode node);
        T visit(TypeScriptArrayNode node);
        T visit(TypeScriptArrowFunctionExpressionNode node);
        T visit(TypeScriptBoolLiteralNode node);
        T visit(TypeScriptCallNode node);
        T visit(TypeScriptCallNewNode node);
        T visit(TypeScriptCastNode node);
        T visit(TypeScriptConditionalNode node);
        T visit(TypeScriptConstructedTypeNode node);
        T visit(TypeScriptEqualsNode node);
        T visit(TypeScriptIndexNode node);
        T visit(TypeScriptLogicalAndNode node);
        T visit(TypeScriptLogicalNotNode node);
        T visit(TypeScriptLogicalOrNode node);
        T visit(TypeScriptNonNullAssertionNode node);
        T visit(TypeScriptNullLiteralNode node);
        T visit(TypeScriptNumberLiteralNode node);
        T visit(TypeScriptObjectLiteralNode node);
        T visit(TypeScriptFunctionExpressionNode node);
        T visit(TypeScriptPropertyAccessNode node);
        T visit(TypeScriptReferenceNode node);
        T visit(TypeScriptStrictEqualsNode node);
        T visit(TypeScriptStrictNotEqualNode node);
        T visit(TypeScriptStringLiteralNode node);
        T visit(TypeScriptSubtractNode node);
        T visit(TypeScriptUnionNode node);
    }
}
