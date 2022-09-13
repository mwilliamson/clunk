package org.zwobble.clunk.backends.python.ast;

import java.util.List;

public record PythonListNode(List<PythonExpressionNode> elements) implements PythonExpressionNode {
    @Override
    public PythonPrecedence precedence() {
        return PythonPrecedence.PRIMARY;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
