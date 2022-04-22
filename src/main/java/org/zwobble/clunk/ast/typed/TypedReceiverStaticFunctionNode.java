package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record TypedReceiverStaticFunctionNode(
    List<String> namespaceName,
    String functionName,
    Source source
) implements TypedNode {
}
