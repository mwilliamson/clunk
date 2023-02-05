package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record TypedArgsNode(
    List<TypedExpressionNode> positional,
    Source source
) implements TypedNode {
}
