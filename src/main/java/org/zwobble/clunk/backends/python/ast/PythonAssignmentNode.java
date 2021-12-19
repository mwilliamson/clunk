package org.zwobble.clunk.backends.python.ast;

import java.util.Optional;

public record PythonAssignmentNode(
    String name,
    Optional<PythonExpressionNode> type,
    Optional<PythonExpressionNode> expression
) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
