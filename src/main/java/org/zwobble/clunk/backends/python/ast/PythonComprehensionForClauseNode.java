package org.zwobble.clunk.backends.python.ast;

public record PythonComprehensionForClauseNode(
    String target,
    PythonExpressionNode iterable
) implements PythonNode {
}
