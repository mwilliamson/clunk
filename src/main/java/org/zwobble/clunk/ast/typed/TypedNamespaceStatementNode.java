package org.zwobble.clunk.ast.typed;

public interface TypedNamespaceStatementNode extends TypedNode {
    interface Visitor<T> {
        T visit(TypedFunctionNode node);
        T visit(TypedRecordNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
