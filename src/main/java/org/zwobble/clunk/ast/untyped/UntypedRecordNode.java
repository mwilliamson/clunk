package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;

import java.util.ArrayList;
import java.util.List;

public record UntypedRecordNode(String name, List<UntypedRecordFieldNode> fields, Source source) implements UntypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(String name) {
        return new Builder(name, List.of(), NullSource.INSTANCE);
    }

    public static record Builder(String name, List<UntypedRecordFieldNode> fields, Source source) {
        public UntypedRecordNode build() {
            return new UntypedRecordNode(name, fields, source);
        }

        public Builder addField(UntypedRecordFieldNode field) {
            var fields = new ArrayList<>(this.fields);
            fields.add(field);
            return new Builder(name, fields, source);
        }
    }
}