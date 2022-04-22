package org.zwobble.clunk.backends.python.ast;

import java.math.BigInteger;

public record PythonIntLiteralNode(BigInteger value) implements PythonExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
