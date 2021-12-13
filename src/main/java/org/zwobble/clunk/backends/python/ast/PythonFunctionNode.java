package org.zwobble.clunk.backends.python.ast;

import java.util.ArrayList;
import java.util.List;

public record PythonFunctionNode(String name, List<String> params) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("f", List.of());
    }

    public static record Builder(String name, List<String> params) {
        public PythonFunctionNode build() {
            return new PythonFunctionNode(name, params);
        }

        public PythonFunctionNode.Builder addParam(String param) {
            var params = new ArrayList<>(this.params);
            params.add(param);
            return new Builder(name, params);
        }

        public PythonFunctionNode.Builder name(String name) {
            return new Builder(name, params);
        }
    }
}
