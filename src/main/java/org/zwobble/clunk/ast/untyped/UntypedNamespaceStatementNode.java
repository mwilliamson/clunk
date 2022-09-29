package org.zwobble.clunk.ast.untyped;

public interface UntypedNamespaceStatementNode extends UntypedNode {
    boolean isTypeDefinition();

    interface Visitor<T> {
        T visit(UntypedBlankLineNode node);
        T visit(UntypedEnumNode node);
        T visit(UntypedFunctionNode node);
        T visit(UntypedInterfaceNode node);
        T visit(UntypedRecordNode node);
        T visit(UntypedSingleLineCommentNode node);
        T visit(UntypedTestNode node);
        T visit(UntypedTestSuiteNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
