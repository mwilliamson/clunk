package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Type;

public record TypedCallStaticFunctionNode(
    TypedExpressionNode receiver,
    TypedArgsNode args,
    StaticFunctionType receiverType,
    Source source
) implements TypedExpressionNode {
    @Override
    public Type type() {
        return receiverType.returnType();
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
