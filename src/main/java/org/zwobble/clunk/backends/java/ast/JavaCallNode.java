package org.zwobble.clunk.backends.java.ast;

import java.util.List;

public record JavaCallNode(
    JavaExpressionNode receiver,
    List<JavaExpressionNode> args
) implements JavaExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
