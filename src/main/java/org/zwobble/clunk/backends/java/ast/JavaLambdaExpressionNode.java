package org.zwobble.clunk.backends.java.ast;

import java.util.List;

public record JavaLambdaExpressionNode(
    List<String> params,
    JavaExpressionNode body
) implements JavaExpressionNode {
    @Override
    public JavaPrecedence precedence() {
        return JavaPrecedence.PRIMARY;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
