package org.zwobble.clunk.backends.java.ast;

public interface JavaAnnotationNode extends JavaNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(JavaMarkerAnnotationNode node);
        T visit(JavaSingleElementAnnotation node);
    }
}
