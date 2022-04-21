package org.zwobble.clunk.backends.typescript.ast;

import java.util.ArrayList;
import java.util.List;

public record TypeScriptFunctionExpressionNode(
    List<TypeScriptParamNode> params,
    List<TypeScriptStatementNode> body
) implements TypeScriptExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder(List.of(), List.of());
    }

    public static record Builder(
        List<TypeScriptParamNode> params,
        List<TypeScriptStatementNode> body
    ) {
        public TypeScriptFunctionExpressionNode build() {
            return new TypeScriptFunctionExpressionNode(params, body);
        }

        public Builder addParam(TypeScriptParamNode param) {
            var params = new ArrayList<>(this.params);
            params.add(param);
            return new Builder(params, body);
        }

        public Builder addBodyStatement(TypeScriptStatementNode statement) {
            var body = new ArrayList<>(this.body);
            body.add(statement);
            return new Builder(params, body);
        }

        public Builder addBodyStatements(List<TypeScriptStatementNode> statements) {
            var body = new ArrayList<>(this.body);
            body.addAll(statements);
            return new Builder(params, body);
        }
    }
}
