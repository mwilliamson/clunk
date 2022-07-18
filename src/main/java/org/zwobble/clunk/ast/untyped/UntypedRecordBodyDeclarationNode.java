package org.zwobble.clunk.ast.untyped;

public interface UntypedRecordBodyDeclarationNode extends UntypedNode {
    interface Visitor<T> {
        T visit(UntypedPropertyNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
