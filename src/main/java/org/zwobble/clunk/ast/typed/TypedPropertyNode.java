package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record TypedPropertyNode(
    String name,
    TypedTypeLevelExpressionNode type,
    List<TypedFunctionStatementNode> body,
    Source source
) implements TypedRecordBodyDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
