package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptCallNewNode(
    TypeScriptExpressionNode receiver,
    List<TypeScriptExpressionNode> typeArgs,
    List<TypeScriptExpressionNode> args
) implements TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.CALL;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
