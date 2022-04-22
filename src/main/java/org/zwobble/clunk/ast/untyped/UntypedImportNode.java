package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedImportNode(List<String> name, Source source) implements UntypedNode {
}
