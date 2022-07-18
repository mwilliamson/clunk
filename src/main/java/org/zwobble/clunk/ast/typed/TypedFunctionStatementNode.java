package org.zwobble.clunk.ast.typed;

public interface TypedFunctionStatementNode extends TypedNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(TypedBlankLineNode node);
        T visit(TypedExpressionStatementNode node);
        T visit(TypedIfStatementNode node);
        T visit(TypedReturnNode node);
        T visit(TypedVarNode node);
    }
}
