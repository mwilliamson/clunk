package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptArrayNode(
    List<TypeScriptExpressionNode> elements
) implements TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.PRIMARY;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
