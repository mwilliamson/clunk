package org.zwobble.clunk.ast.typed;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.util.P;

import java.util.List;

public record TypedTestSuiteNode(
    String name,
    List<TypedNamespaceStatementNode> body,
    Source source
) implements TypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("f", P.vector());
    }

    public record Builder(String name, PVector<TypedNamespaceStatementNode> body) {
        public TypedTestSuiteNode build() {
            return new TypedTestSuiteNode(name, body, NullSource.INSTANCE);
        }

        public Builder name(String name) {
            return new Builder(name, body);
        }

        public Builder addBodyStatement(TypedNamespaceStatementNode statement) {
            return new Builder(name, body.plus(statement));
        }
    }
}
