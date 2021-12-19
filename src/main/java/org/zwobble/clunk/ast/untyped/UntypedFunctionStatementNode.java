package org.zwobble.clunk.ast.untyped;

public interface UntypedFunctionStatementNode extends UntypedNode {
    <T> T accept(Visitor<T> visitor);

    interface Visitor<T> {
        T visit(UntypedReturnNode node);
        T visit(UntypedVarNode node);
    }
}
