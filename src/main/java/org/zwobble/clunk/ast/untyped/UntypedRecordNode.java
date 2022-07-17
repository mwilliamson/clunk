package org.zwobble.clunk.ast.untyped;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.util.P;

import java.util.List;

public record UntypedRecordNode(
    String name,
    List<UntypedRecordFieldNode> fields,
    List<UntypedTypeLevelExpressionNode> supertypes,
    Source source
) implements UntypedNamespaceStatementNode {
    @Override
    public boolean isTypeDefinition() {
        return true;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(String name) {
        return new Builder(name, P.vector(), P.vector(), NullSource.INSTANCE);
    }

    public static record Builder(
        String name,
        PVector<UntypedRecordFieldNode> fields,
        PVector<UntypedTypeLevelExpressionNode> supertypes,
        Source source
    ) {
        public UntypedRecordNode build() {
            return new UntypedRecordNode(name, fields, supertypes, source);
        }

        public Builder addField(UntypedRecordFieldNode field) {
            var fields = this.fields.plus(field);
            return new Builder(name, fields, supertypes, source);
        }

        public Builder addSupertype(UntypedTypeLevelReferenceNode supertype) {
            var supertypes = this.supertypes.plus(supertype);
            return new Builder(name, fields, supertypes, source);
        }
    }
}
