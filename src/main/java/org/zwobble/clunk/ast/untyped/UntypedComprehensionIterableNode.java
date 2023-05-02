package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedComprehensionIterableNode(
    String targetName,
    UntypedExpressionNode iterable,
    List<UntypedExpressionNode> conditions,
    Source source
) implements UntypedNode {
}
