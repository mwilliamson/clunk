package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

import java.util.List;

public record TypedComprehensionForClauseNode(
    String targetName,
    Type targetType,
    TypedExpressionNode iterable,
    List<TypedComprehensionIfClauseNode> ifClauses,
    Source source
) implements TypedNode {
}
