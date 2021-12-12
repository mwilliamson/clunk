package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record TypedFunctionNode(
    String name,
    List<TypedArgNode> args,
    TypedStaticExpressionNode returnType,
    Source source
) implements TypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
