package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptImportNamespaceNode(String module, String name) implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
