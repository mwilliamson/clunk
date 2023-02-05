package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

import java.util.List;
import java.util.Optional;

public record TypedCallConstructorNode(
    TypedExpressionNode receiver,
    Optional<List<TypedTypeLevelExpressionNode>> typeArgs,
    TypedArgsNode args,
    Type type,
    Source source
) implements TypedExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
