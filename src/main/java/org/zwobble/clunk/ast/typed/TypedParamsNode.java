package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record TypedParamsNode(
    List<TypedParamNode> positional,
    Source source
) implements TypedNode {
}
