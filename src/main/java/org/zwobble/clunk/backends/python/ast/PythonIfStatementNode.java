package org.zwobble.clunk.backends.python.ast;

import java.util.List;

public record PythonIfStatementNode(
    List<PythonConditionalBranchNode> conditionalBranches,
    List<PythonStatementNode> elseBody
) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
