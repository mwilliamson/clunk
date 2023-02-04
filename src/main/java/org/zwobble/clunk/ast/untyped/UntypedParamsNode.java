package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedParamsNode(
    List<UntypedParamNode> positional,
    Source source
) implements UntypedNode {
}
