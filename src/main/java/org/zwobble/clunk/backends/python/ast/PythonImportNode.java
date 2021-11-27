package org.zwobble.clunk.backends.python.ast;

public record PythonImportNode(String moduleName) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
