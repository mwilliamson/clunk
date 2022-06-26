package org.zwobble.clunk.ast.untyped;

public interface UntypedTypeLevelExpressionNode extends UntypedNode {
    interface Visitor<T> {
        T visit(UntypedParameterizedTypeNode node);
        T visit(UntypedTypeLevelReferenceNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
