package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedNamedArgNode(
    String name,
    UntypedExpressionNode expression,
    Source source
) implements UntypedNode {
}
