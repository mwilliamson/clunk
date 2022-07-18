package org.zwobble.clunk.backends.typescript.ast;

public interface TypeScriptClassBodyDeclarationNode extends TypeScriptNode {
    interface Visitor<T> {
        T visit(TypeScriptFunctionDeclarationNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
