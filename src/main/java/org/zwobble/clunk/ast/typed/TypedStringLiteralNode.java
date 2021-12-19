package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

public record TypedStringLiteralNode(
    String value,
    Source source
) implements TypedExpressionNode {
    @Override
    public Type type() {
        return StringType.INSTANCE;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
