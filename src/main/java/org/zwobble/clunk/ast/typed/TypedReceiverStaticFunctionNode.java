package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceType;

public record TypedReceiverStaticFunctionNode(
    NamespaceType namespaceType,
    String functionName,
    Source source
) implements TypedNode {
}
