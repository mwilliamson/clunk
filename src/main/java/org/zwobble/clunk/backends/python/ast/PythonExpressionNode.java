package org.zwobble.clunk.backends.python.ast;

public interface PythonExpressionNode extends PythonNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(PythonAttrAccessNode node);
        T visit(PythonReferenceNode node);
    }
}
