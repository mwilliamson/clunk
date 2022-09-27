package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

public record TypedStaticMethodToFunctionNode(
    TypedExpressionNode method,
    Type type
) implements TypedExpressionNode {
    @Override
    public Source source() {
        return method.source();
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
