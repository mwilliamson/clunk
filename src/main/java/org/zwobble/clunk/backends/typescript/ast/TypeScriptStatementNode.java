package org.zwobble.clunk.backends.typescript.ast;

public interface TypeScriptStatementNode extends TypeScriptNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(TypeScriptInterfaceDeclarationNode node);
    }
}
