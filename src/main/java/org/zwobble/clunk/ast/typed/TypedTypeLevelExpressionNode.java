package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.TypeLevelValue;

public record TypedTypeLevelExpressionNode(TypeLevelValue value, Source source) implements TypedNode {
}
