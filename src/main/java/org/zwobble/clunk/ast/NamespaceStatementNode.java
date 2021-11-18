package org.zwobble.clunk.ast;

public interface NamespaceStatementNode extends Node {
    interface Visitor<T> {
        T visit(RecordNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
