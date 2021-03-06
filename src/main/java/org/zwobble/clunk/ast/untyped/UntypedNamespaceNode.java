package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceName;

import java.util.ArrayList;
import java.util.List;

public record UntypedNamespaceNode(
    NamespaceName name,
    List<UntypedImportNode> imports,
    List<UntypedNamespaceStatementNode> statements,
    Source source
) implements UntypedNode {
    public static Builder builder(NamespaceName name) {
        return new Builder(name, List.of(), List.of(), NullSource.INSTANCE);
    }

    public static record Builder(
        NamespaceName name,
        List<UntypedImportNode> imports,
        List<UntypedNamespaceStatementNode> statements,
        Source source
    ) {
        public UntypedNamespaceNode build() {
            return new UntypedNamespaceNode(name, imports, statements, source);
        }

        public Builder addImport(UntypedImportNode import_) {
            var imports = new ArrayList<>(this.imports);
            imports.add(import_);
            return new Builder(name, imports, statements, source);
        }

        public Builder addStatement(UntypedNamespaceStatementNode statement) {
            var statements = new ArrayList<>(this.statements);
            statements.add(statement);
            return new Builder(name, imports, statements, source);
        }
    }
}
