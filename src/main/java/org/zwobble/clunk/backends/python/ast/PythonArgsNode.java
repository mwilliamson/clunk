package org.zwobble.clunk.backends.python.ast;

import java.util.List;

public record PythonArgsNode(
    List<PythonExpressionNode> positional,
    List<PythonKeywordArgumentNode> keyword
) implements PythonNode {
}
