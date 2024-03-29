package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedSingleLineCommentNode(
    String value,
    Source source
) implements UntypedFunctionStatementNode, UntypedNamespaceStatementNode, UntypedRecordBodyDeclarationNode {
    @Override
    public boolean isTypeDefinition() {
        return false;
    }

    @Override
    public <T> T accept(UntypedFunctionStatementNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(UntypedNamespaceStatementNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(UntypedRecordBodyDeclarationNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
