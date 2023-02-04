package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedArgsNode(
    List<UntypedExpressionNode> positional,
    List<UntypedNamedArgNode> named,
    Source source
) implements UntypedNode {
}
