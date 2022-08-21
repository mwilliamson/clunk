package org.zwobble.clunk.backends.typescript.ast;

public interface TypeScriptClassBodyDeclarationNode extends TypeScriptNode {
    interface Visitor<T> {
        T visit(TypeScriptBlankLineNode node);
        T visit(TypeScriptFunctionDeclarationNode node);
        T visit(TypeScriptGetterNode node);
        T visit(TypeScriptSingleLineCommentNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
