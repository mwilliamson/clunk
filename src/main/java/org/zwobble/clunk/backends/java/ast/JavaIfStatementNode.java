package org.zwobble.clunk.backends.java.ast;

import java.util.List;

public record JavaIfStatementNode(
    List<JavaConditionalBranchNode> conditionalBranches,
    List<JavaStatementNode> elseBody
) implements JavaStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
