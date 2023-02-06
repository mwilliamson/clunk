package org.zwobble.clunk.backends.python.ast;

import java.util.List;

public record PythonParamsNode(
    boolean hasSelf,
    List<String> positional,
    List<String> keyword
) implements PythonNode {
}
