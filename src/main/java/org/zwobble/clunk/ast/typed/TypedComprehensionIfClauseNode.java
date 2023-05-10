package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

import java.util.Optional;

public record TypedComprehensionIfClauseNode(
    TypedExpressionNode condition,
    Optional<TypedTypeNarrowNode> typeNarrow,
    Source source
) implements TypedNode {
}
