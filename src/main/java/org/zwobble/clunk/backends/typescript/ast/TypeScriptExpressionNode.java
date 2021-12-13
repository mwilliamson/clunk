package org.zwobble.clunk.backends.typescript.ast;

public interface TypeScriptExpressionNode extends TypeScriptNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(TypeScriptBoolLiteralNode node);
        T visit(TypeScriptReferenceNode node);
        T visit(TypeScriptStringLiteralNode node);
    }
}
