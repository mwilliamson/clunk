package org.zwobble.clunk.backends.python.ast;

import java.util.Optional;

public record PythonExpressionStatementNode(
    PythonExpressionNode expression
) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
