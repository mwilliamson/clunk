package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.ast.SourceType;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.NamespaceType;
import org.zwobble.clunk.types.Type;

import java.util.ArrayList;
import java.util.List;

public record TypedNamespaceNode(
    NamespaceName name,
    List<TypedImportNode> imports,
    List<TypedNamespaceStatementNode> statements,
    NamespaceType type,
    SourceType sourceType,
    Source source
) implements TypedNode {
    public static TypedNamespaceNode.Builder builder(NamespaceName name) {
        return new TypedNamespaceNode.Builder(
            name,
            List.of(),
            List.of(),
            NamespaceType.builder(name),
            SourceType.SOURCE,
            NullSource.INSTANCE
        );
    }

    public static record Builder(
        NamespaceName name,
        List<TypedImportNode> imports,
        List<TypedNamespaceStatementNode> statements,
        NamespaceType.Builder type,
        SourceType sourceType,
        Source source
    ) {
        public TypedNamespaceNode build() {
            return new TypedNamespaceNode(name, imports, statements, type.build(), sourceType, source);
        }

        public Builder addImport(TypedImportNode import_) {
            var imports = new ArrayList<>(this.imports);
            imports.add(import_);
            return new TypedNamespaceNode.Builder(name, imports, statements, type, sourceType, source);
        }

        public Builder addStatement(TypedNamespaceStatementNode statement) {
            var statements = new ArrayList<>(this.statements);
            statements.add(statement);
            return new TypedNamespaceNode.Builder(name, imports, statements, type, sourceType, source);
        }

        public Builder addFieldType(String fieldName, Type fieldType) {
            return new Builder(name, imports, statements, type.addField(fieldName, fieldType), sourceType, source);
        }
    }
}
