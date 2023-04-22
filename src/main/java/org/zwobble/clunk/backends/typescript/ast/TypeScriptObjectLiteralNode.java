package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptObjectLiteralNode(
    List<TypeScriptPropertyLiteralNode> properties
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
