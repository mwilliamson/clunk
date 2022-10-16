package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

public record TypedInstanceOfNode(
    TypedExpressionNode expression,
    TypedTypeLevelExpressionNode typeExpression,
    Source source
) implements TypedExpressionNode {
    @Override
    public Type type() {
        return Types.BOOL;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
