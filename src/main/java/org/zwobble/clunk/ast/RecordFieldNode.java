package org.zwobble.clunk.ast;

import org.zwobble.clunk.sources.Source;

public record RecordFieldNode(String name, StaticExpressionNode type, Source source) implements Node {
}
