package org.zwobble.clunk.backends.python.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;

public record PythonFunctionNode(
    String name,
    List<String> params,
    List<PythonStatementNode> body
) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("f", P.vector(), P.vector());
    }

    public static record Builder(
        String name,
        PVector<String> params,
        PVector<PythonStatementNode> body
    ) {
        public PythonFunctionNode build() {
            return new PythonFunctionNode(name, params, body);
        }

        public Builder addParam(String param) {
            return new Builder(name, params.plus(param), body);
        }

        public Builder name(String name) {
            return new Builder(name, params, body);
        }

        public Builder addBodyStatement(PythonStatementNode statement) {
            return new Builder(name, params, body.plus(statement));
        }
    }
}
