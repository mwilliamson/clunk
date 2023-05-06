package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.List;

public record TypedListComprehensionNode(
    List<TypedComprehensionIterableNode> iterables,
    TypedExpressionNode yield,
    Source source
) implements TypedExpressionNode {
    @Override
    public Type type() {
        return Types.list(yield.type());
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
