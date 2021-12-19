package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedVarNode(
    String name,
    UntypedExpressionNode expression,
    Source source
) implements UntypedFunctionStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
