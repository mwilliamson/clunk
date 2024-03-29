package org.zwobble.clunk.backends.python.ast;

import java.util.List;

public record PythonImportNode(List<String> moduleName) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
