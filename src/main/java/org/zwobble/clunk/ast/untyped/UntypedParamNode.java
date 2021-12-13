package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedParamNode(
    String name,
    UntypedStaticExpressionNode type,
    Source source
) implements UntypedNode {
}
