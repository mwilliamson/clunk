package org.zwobble.clunk.ast.untyped;

public interface UntypedRecordBodyDeclarationNode extends UntypedNode {
    interface Visitor<T> {
        T visit(UntypedBlankLineNode node);
        T visit(UntypedFunctionNode node);
        T visit(UntypedPropertyNode node);
        T visit(UntypedSingleLineCommentNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
