package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedComprehensionIterableNode(
    String targetName,
    UntypedExpressionNode iterable,
    Source source
) implements UntypedNode {
}
