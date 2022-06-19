package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptIfStatementNode(
    List<TypeScriptConditionalBranchNode> conditionalBranches,
    List<TypeScriptStatementNode> elseBody
) implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
