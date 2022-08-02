package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptSwitchNode(
    TypeScriptExpressionNode expression,
    List<TypeScriptSwitchCaseNode> cases
) implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
