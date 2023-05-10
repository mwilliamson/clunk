package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

import java.util.Optional;

public record TypedComprehensionIfClauseNode(
    TypedExpressionNode condition,
    Optional<Type> narrowedTargetType,
    Source source
) implements TypedNode {
}
