package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

public record TypedInterfaceNode(String name, Source source) implements TypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
