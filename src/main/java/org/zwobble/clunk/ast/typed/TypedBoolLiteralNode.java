package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.Type;

public record TypedBoolLiteralNode(boolean value, Source source) implements TypedExpressionNode {
    @Override
    public Type type() {
        return BoolType.INSTANCE;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
