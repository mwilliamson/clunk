package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Type;

import java.util.Optional;

public record TypedImportNode(
    NamespaceName namespaceName,
    Optional<String> fieldName,
    Type type,
    Source source
) implements TypedNode {
}
