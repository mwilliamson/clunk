package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

public record TypedCastUnsafeNode(
    TypedExpressionNode expression,
    TypedTypeLevelExpressionNode typeExpression,
    Type type,
    Source source
) implements TypedExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
