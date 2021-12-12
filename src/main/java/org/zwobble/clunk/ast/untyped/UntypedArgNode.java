package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedArgNode(String name, Source source) implements UntypedNode {
}
