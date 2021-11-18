package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;

import java.util.ArrayList;
import java.util.List;

public record UntypedNamespaceNode(
    List<String> name,
    List<UntypedNamespaceStatementNode> statements,
    Source source
) implements UntypedNode {
    public static Builder builder(List<String> name) {
        return new Builder(name, List.of(), NullSource.INSTANCE);
    }

    public static record Builder(
        List<String> name,
        List<UntypedNamespaceStatementNode> statements,
        Source source
    ) {
        public UntypedNamespaceNode build() {
            return new UntypedNamespaceNode(name, statements, source);
        }

        public Builder addStatement(UntypedNamespaceStatementNode statement) {
            var statements = new ArrayList<>(this.statements);
            statements.add(statement);
            return new Builder(name, statements, source);
        }
    }
}
