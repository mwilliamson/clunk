package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedMapEntryLiteralNode(
    UntypedExpressionNode key,
    UntypedExpressionNode value,
    Source source
) implements UntypedNode {
}
