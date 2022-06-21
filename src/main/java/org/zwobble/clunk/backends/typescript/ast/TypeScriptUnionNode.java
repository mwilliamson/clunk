package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptUnionNode(List<TypeScriptExpressionNode> members) implements TypeScriptExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
