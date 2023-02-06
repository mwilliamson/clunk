package org.zwobble.clunk.backends.python.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;

public record PythonFunctionNode(
    String name,
    List<PythonExpressionNode> decorators,
    PythonParamsNode params,
    List<PythonStatementNode> body
) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("f", P.vector(), false, P.vector(), P.vector(), P.vector());
    }

    public record Builder(
        String name,
        PVector<PythonExpressionNode> decorators,
        boolean hasSelf,
        PVector<String> positionalParams,
        PVector<String> keywordParams,
        PVector<PythonStatementNode> body
    ) {
        public PythonFunctionNode build() {
            return new PythonFunctionNode(
                name,
                decorators,
                new PythonParamsNode(
                    hasSelf,
                    positionalParams,
                    keywordParams
                ),
                body
            );
        }

        public Builder name(String name) {
            return new Builder(name, decorators, hasSelf, positionalParams, keywordParams, body);
        }

        public Builder addDecorator(PythonExpressionNode decorator) {
            return new Builder(name, decorators.plus(decorator), hasSelf, positionalParams, keywordParams, body);
        }

        public Builder withSelf() {
            return new Builder(name, decorators, true, positionalParams, keywordParams, body);
        }

        public Builder addPositionalParam(String param) {
            return new Builder(name, decorators, hasSelf, positionalParams.plus(param), keywordParams, body);
        }

        public Builder addKeywordParam(String param) {
            return new Builder(name, decorators, hasSelf, positionalParams, keywordParams.plus(param), body);
        }

        public Builder addBodyStatement(PythonStatementNode statement) {
            return new Builder(name, decorators, hasSelf, positionalParams, keywordParams, body.plus(statement));
        }
    }
}
