package org.zwobble.clunk.ast;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;

import java.util.ArrayList;
import java.util.List;

public record NamespaceNode(
    List<String> name,
    List<NamespaceStatementNode> statements,
    Source source
) implements Node {
    public static Builder builder(List<String> name) {
        return new Builder(name, List.of(), NullSource.INSTANCE);
    }

    public static record Builder(
        List<String> name,
        List<NamespaceStatementNode> statements,
        Source source
    ) {
        public NamespaceNode build() {
            return new NamespaceNode(name, statements, source);
        }

        public Builder addStatement(NamespaceStatementNode statement) {
            var statements = new ArrayList<>(this.statements);
            statements.add(statement);
            return new Builder(name, statements, source);
        }
    }
}
