package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

public record TypedVarNode(
    String name,
    TypedExpressionNode expression,
    Source source
) implements TypedFunctionStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
