package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedBlankLineNode(Source source) implements UntypedFunctionStatementNode, UntypedNamespaceStatementNode {
    @Override
    public boolean isTypeDefinition() {
        return false;
    }

    @Override
    public <T> T accept(UntypedFunctionStatementNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(UntypedNamespaceStatementNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
