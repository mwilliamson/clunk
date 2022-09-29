package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedTestSuiteNode(
    String name,
    List<UntypedNamespaceStatementNode> body,
    Source source
) implements UntypedNamespaceStatementNode {
    @Override
    public boolean isTypeDefinition() {
        return false;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
