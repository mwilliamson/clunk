package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceId;

import java.util.ArrayList;
import java.util.List;

public record UntypedNamespaceNode(
    NamespaceId id,
    List<UntypedImportNode> imports,
    List<UntypedNamespaceStatementNode> statements,
    Source source
) implements UntypedNode {
    public static Builder builder(NamespaceId id) {
        return new Builder(id, List.of(), List.of(), NullSource.INSTANCE);
    }

    public record Builder(
        NamespaceId id,
        List<UntypedImportNode> imports,
        List<UntypedNamespaceStatementNode> statements,
        Source source
    ) {
        public UntypedNamespaceNode build() {
            return new UntypedNamespaceNode(id, imports, statements, source);
        }

        public Builder addImport(UntypedImportNode import_) {
            var imports = new ArrayList<>(this.imports);
            imports.add(import_);
            return new Builder(id, imports, statements, source);
        }

        public Builder addStatement(UntypedNamespaceStatementNode statement) {
            var statements = new ArrayList<>(this.statements);
            statements.add(statement);
            return new Builder(id, imports, statements, source);
        }
    }
}
