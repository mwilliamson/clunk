package org.zwobble.clunk.ast.untyped;

public interface UntypedNamespaceStatementNode extends UntypedNode {
    interface Visitor<T> {
        T visit(UntypedRecordNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
