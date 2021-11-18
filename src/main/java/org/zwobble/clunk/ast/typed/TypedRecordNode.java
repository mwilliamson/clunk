package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;

import java.util.ArrayList;
import java.util.List;

public record TypedRecordNode(
    String name,
    List<TypedRecordFieldNode> fields,
    Source source
) implements TypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(String name) {
        return new Builder(name, List.of(), NullSource.INSTANCE);
    }

    public static record Builder(String name, List<TypedRecordFieldNode> fields, Source source) {
        public TypedRecordNode build() {
            return new TypedRecordNode(name, fields, source);
        }

        public Builder addField(TypedRecordFieldNode field) {
            var fields = new ArrayList<>(this.fields);
            fields.add(field);
            return new Builder(name, fields, source);
        }
    }
}
