package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

public record TypedBoolLiteralNode(boolean value, Source source) implements TypedExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
