package org.zwobble.clunk.backends.python.ast;

import java.util.List;

public record PythonComprehensionForClauseNode(
    String target,
    PythonExpressionNode iterable,
    List<PythonExpressionNode> conditions
) implements PythonNode {
}
