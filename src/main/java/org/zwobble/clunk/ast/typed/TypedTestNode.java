package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.Type;

import java.util.ArrayList;
import java.util.List;

public record TypedTestNode(
    String name,
    List<TypedFunctionStatementNode> body,
    Source source
) implements TypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("f", List.of());
    }

    public static record Builder(
        String name,
        List<TypedFunctionStatementNode> body
    ) {
        public TypedTestNode build() {
            return new TypedTestNode(name, body, NullSource.INSTANCE);
        }

        public Builder name(String name) {
            return new Builder(name, body);
        }

        public Builder addBodyStatement(TypedFunctionStatementNode statement) {
            var body = new ArrayList<>(this.body);
            body.add(statement);
            return new Builder(name, body);
        }
    }
}
