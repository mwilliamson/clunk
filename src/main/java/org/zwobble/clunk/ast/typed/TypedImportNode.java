package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Type;

import java.util.Optional;

import static org.zwobble.clunk.util.Lists.last;

public record TypedImportNode(
    NamespaceName namespaceName,
    Optional<String> fieldName,
    Type type,
    Source source
) implements TypedNode {
    public String variableName() {
        if (fieldName.isPresent()) {
            return fieldName.get();
        } else {
            return last(namespaceName.parts());
        }
    }
}
