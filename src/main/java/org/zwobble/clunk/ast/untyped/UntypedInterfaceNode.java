package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedInterfaceNode(
    String name,
    boolean isSealed,
    Source source
) implements UntypedNamespaceStatementNode {
    @Override
    public boolean isTypeDefinition() {
        return true;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
