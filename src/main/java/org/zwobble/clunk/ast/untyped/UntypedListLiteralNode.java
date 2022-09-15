package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedListLiteralNode(
    List<UntypedExpressionNode> elements,
    Source source
) implements UntypedExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
