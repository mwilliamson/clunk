package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptConstructedTypeNode(
    TypeScriptExpressionNode receiver,
    List<TypeScriptExpressionNode> args
) implements TypeScriptExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
