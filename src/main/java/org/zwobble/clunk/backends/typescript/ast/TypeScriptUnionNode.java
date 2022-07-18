package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptUnionNode(List<? extends TypeScriptExpressionNode> members) implements TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.BITWISE_OR;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
