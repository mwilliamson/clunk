package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

public record TypedStaticExpressionNode(Type type, Source source) implements TypedNode {
}
