package org.zwobble.clunk.backends.python.ast;

public interface PythonBinaryOperationNode extends PythonExpressionNode {
    PythonExpressionNode left();
    PythonExpressionNode right();
}
