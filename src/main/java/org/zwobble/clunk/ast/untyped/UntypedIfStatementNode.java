package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedIfStatementNode(
    List<ConditionalBranch> conditionalBranches,
    List<UntypedFunctionStatementNode> elseBranch,
    Source source
) implements UntypedFunctionStatementNode {
    public record ConditionalBranch(
        UntypedExpressionNode condition,
        List<UntypedFunctionStatementNode> body
    ) {
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
