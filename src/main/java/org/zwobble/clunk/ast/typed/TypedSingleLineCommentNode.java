package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

public record TypedSingleLineCommentNode(
    String value,
    Source source
) implements TypedFunctionStatementNode, TypedNamespaceStatementNode {
    @Override
    public <T> T accept(TypedFunctionStatementNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(TypedNamespaceStatementNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
