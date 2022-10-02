package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedCastUnsafeNode(
    UntypedExpressionNode expression,
    UntypedTypeLevelExpressionNode typeExpression,
    Source source
) implements UntypedExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
