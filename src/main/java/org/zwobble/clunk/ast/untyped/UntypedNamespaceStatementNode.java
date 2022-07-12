package org.zwobble.clunk.ast.untyped;

public interface UntypedNamespaceStatementNode extends UntypedNode {
    boolean isTypeDefinition();

    interface Visitor<T> {
        T visit(UntypedEnumNode node);
        T visit(UntypedFunctionNode node);
        T visit(UntypedInterfaceNode node);
        T visit(UntypedRecordNode node);
        T visit(UntypedTestNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
