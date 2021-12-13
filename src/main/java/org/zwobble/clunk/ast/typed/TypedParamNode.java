package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

public record TypedParamNode(String name, TypedStaticExpressionNode type, Source source) implements TypedNode {
}
