package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record TypedSwitchNode(
    TypedReferenceNode expression,
    List<TypedSwitchCaseNode> cases,
    boolean returns,
    Source source
) implements TypedFunctionStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
