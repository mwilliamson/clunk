package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedMemberAccessNode(
    UntypedExpressionNode receiver,
    String memberName,
    Source operatorSource,
    Source source
) implements UntypedExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
