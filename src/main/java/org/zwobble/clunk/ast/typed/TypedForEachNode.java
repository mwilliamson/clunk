package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

import java.util.List;

public record TypedForEachNode(
    String targetName,
    Type targetType,
    TypedExpressionNode iterable,
    List<TypedFunctionStatementNode> body,
    Source source
) implements TypedFunctionStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
