package org.zwobble.clunk.backends.python.ast;

import java.util.List;

public record PythonModuleNode(
    String name,
    List<PythonStatementNode> statements
) implements PythonNode {

}
