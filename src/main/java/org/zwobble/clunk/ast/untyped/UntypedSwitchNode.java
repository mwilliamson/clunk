package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedSwitchNode(
    UntypedReferenceNode expression,
    List<UntypedSwitchCaseNode> cases,
    Source source
) implements UntypedFunctionStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
