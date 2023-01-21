package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

public record TypedMapEntryLiteralNode(
    TypedExpressionNode key,
    TypedExpressionNode value,
    Source source
) implements TypedNode {
    public Type keyType() {
        return key.type();
    }

    public Type valueType() {
        return value.type();
    }
}
