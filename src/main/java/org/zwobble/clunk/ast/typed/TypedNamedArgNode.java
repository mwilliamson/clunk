package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

public record TypedNamedArgNode(
    String name,
    TypedExpressionNode expression,
    Source source
) implements TypedNode {
}
