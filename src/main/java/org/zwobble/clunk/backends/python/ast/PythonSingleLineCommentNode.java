package org.zwobble.clunk.backends.python.ast;

public record PythonSingleLineCommentNode(String value) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
