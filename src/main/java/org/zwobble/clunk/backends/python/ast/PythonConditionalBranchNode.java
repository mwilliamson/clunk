package org.zwobble.clunk.backends.python.ast;

import java.util.List;

public record PythonConditionalBranchNode(
    PythonExpressionNode condition,
    List<PythonStatementNode> body
) implements PythonNode {
}
