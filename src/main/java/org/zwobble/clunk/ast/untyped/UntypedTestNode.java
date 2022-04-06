package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;

import java.util.ArrayList;
import java.util.List;

public record UntypedTestNode(
    String name,
    List<UntypedFunctionStatementNode> body,
    Source source
) implements UntypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static UntypedTestNode.Builder builder() {
        return new UntypedTestNode.Builder(
            "f",
            List.of(),
            NullSource.INSTANCE
        );
    }

    public static record Builder(
        String name,
        List<UntypedFunctionStatementNode> body,
        Source source
    ) {
        public UntypedTestNode build() {
            return new UntypedTestNode(name, body, source);
        }

        public Builder name(String name) {
            return new Builder(name, body, source);
        }

        public Builder addBodyStatement(UntypedFunctionStatementNode statement) {
            var body = new ArrayList<>(this.body);
            body.add(statement);
            return new Builder(name, body, source);
        }
    }

}
