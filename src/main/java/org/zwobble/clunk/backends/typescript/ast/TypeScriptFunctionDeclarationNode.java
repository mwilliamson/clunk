package org.zwobble.clunk.backends.typescript.ast;

import java.util.ArrayList;
import java.util.List;

public record TypeScriptFunctionDeclarationNode(
    String name,
    List<TypeScriptParamNode> params,
    TypeScriptExpressionNode returnType
) implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("f", List.of(), TypeScript.reference("void"));
    }

    public static record Builder(
        String name,
        List<TypeScriptParamNode> params,
        TypeScriptExpressionNode returnType
    ) {
        public TypeScriptFunctionDeclarationNode build() {
            return new TypeScriptFunctionDeclarationNode(name, params, returnType);
        }

        public Builder name(String name) {
            return new Builder(name, params, returnType);
        }

        public Builder returnType(TypeScriptExpressionNode returnType) {
            return new Builder(name, params, returnType);
        }

        public Builder addParam(TypeScriptParamNode param) {
            var params = new ArrayList<>(this.params);
            params.add(param);
            return new Builder(name, params, returnType);
        }
    }
}
