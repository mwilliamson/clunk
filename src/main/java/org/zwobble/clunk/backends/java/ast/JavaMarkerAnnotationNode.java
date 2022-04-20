package org.zwobble.clunk.backends.java.ast;

public record JavaMarkerAnnotationNode(JavaTypeExpressionNode type) implements JavaAnnotationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
