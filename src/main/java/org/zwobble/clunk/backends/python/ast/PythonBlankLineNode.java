package org.zwobble.clunk.backends.python.ast;

public record PythonBlankLineNode() implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
