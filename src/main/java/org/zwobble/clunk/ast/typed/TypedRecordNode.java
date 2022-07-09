package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.RecordType;

import java.util.List;

public record TypedRecordNode(
    String name,
    RecordType type,
    Source source
) implements TypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(String name) {
        return builder(NamespaceName.fromParts(), name);
    }

    public static Builder builder(NamespaceName namespaceName, String name) {
        return new Builder(name, List.of(), new RecordType(namespaceName, name), NullSource.INSTANCE);
    }

    public static record Builder(String name, List<TypedRecordFieldNode> fields, RecordType type, Source source) {
        public TypedRecordNode build() {
            return new TypedRecordNode(name, type, source);
        }
    }
}
