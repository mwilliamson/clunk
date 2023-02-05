package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

import java.util.List;
import java.util.stream.Stream;

public record TypedParamsNode(
    List<TypedParamNode> positional,
    List<TypedParamNode> named,
    Source source
) implements TypedNode {
    public List<TypedParamNode> all() {
        return Stream.concat(positional.stream(), named.stream()).toList();
    }
}
