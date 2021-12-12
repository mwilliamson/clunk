package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedFunctionNode(
    String name,
    List<UntypedArgNode> args,
    UntypedStaticExpressionNode returnType,
    Source source
) implements UntypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
