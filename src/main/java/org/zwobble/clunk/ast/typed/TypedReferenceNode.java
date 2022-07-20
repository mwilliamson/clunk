package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

public record TypedReferenceNode(String name, Type type, boolean isMember, Source source) implements TypedExpressionNode {

    public static TypedReferenceNode member(String name, Type type, Source source) {
        return new TypedReferenceNode(name, type, true, source);
    }

    public static TypedReferenceNode variable(String name, Type type, Source source) {
        return new TypedReferenceNode(name, type, false, source);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
