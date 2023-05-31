package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

public record UntypedFunctionSignatureNode(
    String name,
    UntypedParamsNode params,
    UntypedTypeLevelExpressionNode returnType,
    Source source
) implements UntypedInterfaceBodyDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
