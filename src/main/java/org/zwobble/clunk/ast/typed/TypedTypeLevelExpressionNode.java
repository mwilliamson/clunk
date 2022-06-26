package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

public record TypedTypeLevelExpressionNode(Type type, Source source) implements TypedNode {
}
