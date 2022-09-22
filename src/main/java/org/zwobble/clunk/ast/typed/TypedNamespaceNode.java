package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.ast.SourceType;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceName;

import java.util.ArrayList;
import java.util.List;

public record TypedNamespaceNode(
    NamespaceName name,
    List<TypedImportNode> imports,
    List<TypedNamespaceStatementNode> statements,
    SourceType sourceType,
    Source source
) implements TypedNode {
    public static TypedNamespaceNode.Builder builder(NamespaceName name) {
        return new TypedNamespaceNode.Builder(name, List.of(), List.of(), SourceType.SOURCE, NullSource.INSTANCE);
    }

    public static record Builder(
        NamespaceName name,
        List<TypedImportNode> imports,
        List<TypedNamespaceStatementNode> statements,
        SourceType sourceType,
        Source source
    ) {
        public TypedNamespaceNode build() {
            return new TypedNamespaceNode(name, imports, statements, sourceType, source);
        }

        public Builder addImport(TypedImportNode import_) {
            var imports = new ArrayList<>(this.imports);
            imports.add(import_);
            return new TypedNamespaceNode.Builder(name, imports, statements, sourceType, source);
        }

        public Builder addStatement(TypedNamespaceStatementNode statement) {
            var statements = new ArrayList<>(this.statements);
            statements.add(statement);
            return new TypedNamespaceNode.Builder(name, imports, statements, sourceType, source);
        }
    }
}
