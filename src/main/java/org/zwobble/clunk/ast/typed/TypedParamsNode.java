package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record TypedParamsNode(
    List<TypedParamNode> positional,
    List<TypedParamNode> named,
    Source source
) implements TypedNode {
}
