package org.zwobble.clunk.ast.untyped;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.util.P;

import java.util.List;

public record UntypedTestSuiteNode(
    String name,
    List<UntypedNamespaceStatementNode> body,
    Source source
) implements UntypedNamespaceStatementNode {
    @Override
    public boolean isTypeDefinition() {
        return false;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("f", P.vector());
    }

    public record Builder(String name, PVector<UntypedNamespaceStatementNode> body) {
        public UntypedTestSuiteNode build() {
            return new UntypedTestSuiteNode(name, body, NullSource.INSTANCE);
        }

        public Builder addBodyStatement(UntypedNamespaceStatementNode statement) {
            return new Builder(name, body.plus(statement));
        }
    }
}
