package org.zwobble.clunk.ast.typed;

public interface TypedFunctionStatementNode extends TypedNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(TypedReturnNode node);
    }
}