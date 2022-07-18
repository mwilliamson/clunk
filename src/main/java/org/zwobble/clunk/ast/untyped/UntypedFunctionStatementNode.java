package org.zwobble.clunk.ast.untyped;

public interface UntypedFunctionStatementNode extends UntypedNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(UntypedBlankLineNode node);
        T visit(UntypedExpressionStatementNode node);
        T visit(UntypedIfStatementNode node);
        T visit(UntypedReturnNode node);
        T visit(UntypedVarNode node);
    }
}
