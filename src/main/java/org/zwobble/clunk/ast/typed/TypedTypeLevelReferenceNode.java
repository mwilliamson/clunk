package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.TypeLevelValue;

public record TypedTypeLevelReferenceNode(String name, TypeLevelValue value, Source source) implements TypedTypeLevelExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
