package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptForOfNode(
    String targetName,
    TypeScriptExpressionNode iterable,
    List<TypeScriptStatementNode> body
) implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
