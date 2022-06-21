package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedInterfaceNode(String name, Source source) implements UntypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
