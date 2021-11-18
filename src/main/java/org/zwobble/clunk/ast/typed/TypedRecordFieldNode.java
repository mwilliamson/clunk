package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

public record TypedRecordFieldNode(
    String name,
    TypedStaticExpressionNode type,
    Source source
) implements TypedNode {
}
