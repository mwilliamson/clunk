package org.zwobble.clunk.ast.typed;

public interface TypedNamespaceStatementNode extends TypedNode {
    interface Visitor<T> {
        T visit(TypedBlankLineNode node);
        T visit(TypedEnumNode node);
        T visit(TypedFunctionNode node);
        T visit(TypedInterfaceNode node);
        T visit(TypedRecordNode node);
        T visit(TypedSingleLineCommentNode node);
        T visit(TypedTestNode node);
    }

    <T> T accept(Visitor<T> visitor);
}
