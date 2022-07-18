package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.types.TypeLevelValue;

public interface TypedTypeLevelExpressionNode extends TypedNode {
    TypeLevelValue value();

    interface Visitor<T> {
        T visit(TypedConstructedTypeNode node);
        T visit(TypedTypeLevelReferenceNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
