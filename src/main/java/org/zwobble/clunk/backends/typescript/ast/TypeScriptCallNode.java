package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptCallNode(
    TypeScriptExpressionNode receiver,
    List<TypeScriptExpressionNode> args
) implements TypeScriptExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
