package org.zwobble.clunk.backends.typescript.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;

public record TypeScriptFunctionTypeNode(
    List<TypeScriptParamNode> params,
    TypeScriptExpressionNode returnType
) implements TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.ASSIGNMENT;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder(P.vector(), new TypeScriptNullLiteralNode());
    }

    public record Builder(
        PVector<TypeScriptParamNode> params,
        TypeScriptExpressionNode body
    ) {
        public TypeScriptFunctionTypeNode build() {
            return new TypeScriptFunctionTypeNode(params, body);
        }

        public Builder addParam(TypeScriptParamNode param) {
            return new Builder(params.plus(param), body);
        }

        public Builder withReturnType(TypeScriptExpressionNode returnType) {
            return new Builder(params, returnType);
        }
    }
}
