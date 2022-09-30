package org.zwobble.clunk.backends.java.ast;

import java.util.List;

public record JavaForEachNode(
    String targetName,
    JavaExpressionNode iterable,
    List<JavaStatementNode> body
) implements JavaStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
