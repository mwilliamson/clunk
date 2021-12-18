package org.zwobble.clunk.backends.typescript.ast;

import java.util.ArrayList;
import java.util.List;

public record TypeScriptFunctionDeclarationNode(
    String name,
    List<TypeScriptParamNode> params,
    TypeScriptExpressionNode returnType,
    List<TypeScriptStatementNode> body
) implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("f", List.of(), TypeScript.reference("void"), List.of());
    }

    public static record Builder(
        String name,
        List<TypeScriptParamNode> params,
        TypeScriptExpressionNode returnType,
        List<TypeScriptStatementNode> body
    ) {
        public TypeScriptFunctionDeclarationNode build() {
            return new TypeScriptFunctionDeclarationNode(name, params, returnType, body);
        }

        public Builder name(String name) {
            return new Builder(name, params, returnType, body);
        }

        public Builder returnType(TypeScriptExpressionNode returnType) {
            return new Builder(name, params, returnType, body);
        }

        public Builder addParam(TypeScriptParamNode param) {
            var params = new ArrayList<>(this.params);
            params.add(param);
            return new Builder(name, params, returnType, body);
        }

        public Builder addBodyStatement(TypeScriptStatementNode statement) {
            var body = new ArrayList<>(this.body);
            body.add(statement);
            return new Builder(name, params, returnType, body);
        }
    }
}
