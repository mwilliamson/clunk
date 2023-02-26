package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.Type;

import java.util.ArrayList;
import java.util.List;

public record TypedNamespaceNode(
    NamespaceId id,
    List<TypedImportNode> imports,
    List<TypedNamespaceStatementNode> statements,
    NamespaceType type,
    Source source
) implements TypedNode {
    public static TypedNamespaceNode.Builder builder(NamespaceId id) {
        return new TypedNamespaceNode.Builder(
            id,
            List.of(),
            List.of(),
            NamespaceType.builder(id),
            NullSource.INSTANCE
        );
    }

    public static record Builder(
        NamespaceId id,
        List<TypedImportNode> imports,
        List<TypedNamespaceStatementNode> statements,
        NamespaceType.Builder type,
        Source source
    ) {
        public TypedNamespaceNode build() {
            return new TypedNamespaceNode(id, imports, statements, type.build(), source);
        }

        public Builder addImport(TypedImportNode import_) {
            var imports = new ArrayList<>(this.imports);
            imports.add(import_);
            return new TypedNamespaceNode.Builder(id, imports, statements, type, source);
        }

        public Builder addStatement(TypedNamespaceStatementNode statement) {
            var statements = new ArrayList<>(this.statements);
            statements.add(statement);
            return new TypedNamespaceNode.Builder(id, imports, statements, type, source);
        }

        public Builder addFieldType(String fieldName, Type fieldType) {
            return new Builder(id, imports, statements, type.addField(fieldName, fieldType), source);
        }
    }
}
