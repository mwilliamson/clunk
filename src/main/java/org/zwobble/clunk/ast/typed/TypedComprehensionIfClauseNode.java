package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

public record TypedComprehensionIfClauseNode(
    TypedExpressionNode condition,
    Source source
) implements TypedNode {
}
