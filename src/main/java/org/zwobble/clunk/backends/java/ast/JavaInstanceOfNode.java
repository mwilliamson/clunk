package org.zwobble.clunk.backends.java.ast;

import java.util.Optional;

public record JavaInstanceOfNode(
    JavaExpressionNode expression,
    JavaTypeExpressionNode typeExpressionNode,
    Optional<String> targetName
) implements JavaExpressionNode {
    @Override
    public JavaPrecedence precedence() {
        return JavaPrecedence.RELATIONAL;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
