package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;

import java.util.ArrayList;
import java.util.List;

public record TypedNamespaceNode(
    List<String> name,
    List<TypedNamespaceStatementNode> statements,
    Source source
) implements TypedNode {
    public static TypedNamespaceNode.Builder builder(List<String> name) {
        return new TypedNamespaceNode.Builder(name, List.of(), NullSource.INSTANCE);
    }

    public static record Builder(
        List<String> name,
        List<TypedNamespaceStatementNode> statements,
        Source source
    ) {
        public TypedNamespaceNode build() {
            return new TypedNamespaceNode(name, statements, source);
        }

        public TypedNamespaceNode.Builder addStatement(TypedNamespaceStatementNode statement) {
            var statements = new ArrayList<>(this.statements);
            statements.add(statement);
            return new TypedNamespaceNode.Builder(name, statements, source);
        }
    }
}
