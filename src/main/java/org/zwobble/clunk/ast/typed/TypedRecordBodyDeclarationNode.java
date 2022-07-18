package org.zwobble.clunk.ast.typed;

public interface TypedRecordBodyDeclarationNode extends TypedNode {
    interface Visitor<T> {
        T visit(TypedPropertyNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
