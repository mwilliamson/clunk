package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedEnumNode(
    String name,
    List<String> members,
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
