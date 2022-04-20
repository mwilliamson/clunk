package org.zwobble.clunk.backends.java.ast;

public record JavaSingleElementAnnotation(
    JavaTypeExpressionNode type,
    JavaExpressionNode value
) implements JavaAnnotationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
