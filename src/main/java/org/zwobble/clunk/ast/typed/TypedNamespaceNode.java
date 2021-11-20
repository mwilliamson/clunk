package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record TypedNamespaceNode(
    List<String> name,
    List<TypedNamespaceStatementNode> statements,
    Source source
) implements TypedNode {
}
