package org.zwobble.clunk.backends.python.ast;

import java.util.List;

public record PythonCallNode(
    PythonExpressionNode receiver,
    List<PythonExpressionNode> args,
    List<PythonKeywordArgumentNode> kwargs
) implements PythonExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
