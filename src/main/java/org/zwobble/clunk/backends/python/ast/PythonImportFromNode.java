package org.zwobble.clunk.backends.python.ast;

import java.util.List;

public record PythonImportFromNode(List<String> moduleName, List<String> names) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
