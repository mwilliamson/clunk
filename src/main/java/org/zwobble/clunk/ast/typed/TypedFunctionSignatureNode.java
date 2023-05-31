package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.FunctionType;

public record TypedFunctionSignatureNode(
    String name,
    TypedParamsNode params,
    TypedTypeLevelExpressionNode returnType,
    FunctionType type,
    Source source
) implements TypedInterfaceBodyDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
