package org.zwobble.clunk.ast;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;

import java.util.ArrayList;
import java.util.List;

public record RecordNode(String name, List<RecordFieldNode> fields, Source source) implements NamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(String name) {
        return new Builder(name, List.of(), NullSource.INSTANCE);
    }

    public static record Builder(String name, List<RecordFieldNode> fields, Source source) {
        public RecordNode build() {
            return new RecordNode(name, fields, source);
        }

        public Builder addField(RecordFieldNode field) {
            var fields = new ArrayList<>(this.fields);
            fields.add(field);
            return new Builder(name, fields, source);
        }
    }
}
