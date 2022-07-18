package org.zwobble.clunk.backends.python.ast;

import java.math.BigInteger;

public record PythonIntLiteralNode(BigInteger value) implements PythonExpressionNode {
    @Override
    public PythonPrecedence precedence() {
        return PythonPrecedence.PRIMARY;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
