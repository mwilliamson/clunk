package org.zwobble.clunk.backends.java.ast;

import java.util.List;

public record JavaParameterizedType(
    JavaTypeExpressionNode receiver,
    List<JavaTypeExpressionNode> args
) implements JavaTypeExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
