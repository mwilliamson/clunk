package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedRecordFieldNode(String name, UntypedStaticExpressionNode type, Source source) implements UntypedNode {
}
