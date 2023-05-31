package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

public record TypedFunctionSignatureNode(
    String name,
    TypedParamsNode params,
    TypedTypeLevelExpressionNode returnType,
    Source source
) implements TypedInterfaceBodyDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
