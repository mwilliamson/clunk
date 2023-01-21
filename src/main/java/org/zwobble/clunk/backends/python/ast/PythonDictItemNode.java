package org.zwobble.clunk.backends.python.ast;

public record PythonDictItemNode(
    PythonExpressionNode key,
    PythonExpressionNode value
) implements PythonNode {
}
