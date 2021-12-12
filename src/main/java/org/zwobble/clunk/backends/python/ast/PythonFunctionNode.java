package org.zwobble.clunk.backends.python.ast;

import java.util.ArrayList;
import java.util.List;

public record PythonFunctionNode(String name, List<String> args) implements PythonStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("f", List.of());
    }

    public static record Builder(String name, List<String> args) {
        public PythonFunctionNode build() {
            return new PythonFunctionNode(name, args);
        }

        public PythonFunctionNode.Builder addArg(String arg) {
            var args = new ArrayList<>(this.args);
            args.add(arg);
            return new Builder(name, args);
        }

        public PythonFunctionNode.Builder name(String name) {
            return new Builder(name, args);
        }
    }
}
