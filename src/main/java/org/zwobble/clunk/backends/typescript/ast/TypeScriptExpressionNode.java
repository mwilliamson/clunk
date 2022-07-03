package org.zwobble.clunk.backends.typescript.ast;

public interface TypeScriptExpressionNode extends TypeScriptNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(TypeScriptBoolLiteralNode node);
        T visit(TypeScriptCallNode node);
        T visit(TypeScriptConstructedTypeNode node);
        T visit(TypeScriptNumberLiteralNode node);
        T visit(TypeScriptFunctionExpressionNode node);
        T visit(TypeScriptReferenceNode node);
        T visit(TypeScriptStringLiteralNode node);
        T visit(TypeScriptUnionNode node);
    }
}
