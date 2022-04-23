package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceName;

public record UntypedImportNode(NamespaceName name, Source source) implements UntypedNode {
}
