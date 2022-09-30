package org.zwobble.clunk.backends.python.ast;

import java.util.List;

public record PythonForEachNode(
    String targetName,
    PythonExpressionNode iterable,
    List<PythonStatementNode> body
) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
