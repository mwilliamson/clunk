package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record TypedIfStatementNode(
    List<TypedConditionalBranchNode> conditionalBranches,
    List<TypedFunctionStatementNode> elseBody,
    Source source
) implements TypedFunctionStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
