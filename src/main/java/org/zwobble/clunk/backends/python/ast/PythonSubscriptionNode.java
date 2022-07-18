package org.zwobble.clunk.backends.python.ast;

import java.util.List;

public record PythonSubscriptionNode(
    PythonExpressionNode receiver,
    List<PythonExpressionNode> args
) implements PythonExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
