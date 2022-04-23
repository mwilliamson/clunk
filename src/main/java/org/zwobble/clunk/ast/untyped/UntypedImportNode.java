package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceName;

import java.util.Optional;

public record UntypedImportNode(
    NamespaceName namespaceName,
    Optional<String> fieldName,
    Source source
) implements UntypedNode {
}
