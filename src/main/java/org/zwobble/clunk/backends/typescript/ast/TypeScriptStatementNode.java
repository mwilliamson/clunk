package org.zwobble.clunk.backends.typescript.ast;

public interface TypeScriptStatementNode extends TypeScriptNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(TypeScriptExpressionStatementNode node);
        T visit(TypeScriptFunctionDeclarationNode node);
        T visit(TypeScriptIfStatementNode node);
        T visit(TypeScriptImportNode node);
        T visit(TypeScriptInterfaceDeclarationNode node);
        T visit(TypeScriptLetNode node);
        T visit(TypeScriptReturnNode node);
        T visit(TypeScriptTypeDeclarationNode node);
    }
}
