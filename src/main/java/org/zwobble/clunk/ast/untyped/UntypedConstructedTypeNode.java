package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedConstructedTypeNode(
    UntypedTypeLevelExpressionNode receiver,
    List<UntypedTypeLevelExpressionNode> args,
    Source source
) implements UntypedTypeLevelExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
