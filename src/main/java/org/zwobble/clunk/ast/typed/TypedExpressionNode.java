package org.zwobble.clunk.ast.typed;

public interface TypedExpressionNode extends TypedNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(TypedStringLiteralNode node);
    }
}
