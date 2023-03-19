package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

public record TypedMemberDefinitionReferenceNode(
    TypedExpressionNode receiver,
    String memberName,
    Type type,
    Source operatorSource,
    Source source
) implements TypedExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
