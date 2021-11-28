package org.zwobble.clunk.backends.typescript.ast;

public interface TypeScriptExpressionNode extends TypeScriptNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(TypeScriptStringLiteralNode node);
    }
}
