package org.zwobble.clunk.ast.typed;

public interface TypedRecordBodyDeclarationNode extends TypedNode {
    interface Visitor<T> {
        T visit(TypedBlankLineNode node);
        T visit(TypedPropertyNode node);
        T visit(TypedSingleLineCommentNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
