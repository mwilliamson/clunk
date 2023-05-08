package org.zwobble.clunk.backends.typescript.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;

public record TypeScriptArrowFunctionExpressionNode(
    List<TypeScriptParamNode> params,
    TypeScriptExpressionNode body
) implements TypeScriptExpressionNode {
    @Override
    public TypeScriptPrecedence precedence() {
        return TypeScriptPrecedence.PRIMARY;
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
        public TypeScriptArrowFunctionExpressionNode build() {
            return new TypeScriptArrowFunctionExpressionNode(params, body);
        }

        public Builder addParam(TypeScriptParamNode param) {
            return new Builder(params.plus(param), body);
        }

        public Builder withBodyExpression(TypeScriptExpressionNode body) {
            return new Builder(params, body);
        }
    }
}
