package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceName;

import java.util.Optional;

public record TypedImportNode(
    NamespaceName namespaceName,
    Optional<String> fieldName,
    Source source
) implements TypedNode {
}
