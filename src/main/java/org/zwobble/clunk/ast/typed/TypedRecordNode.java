package org.zwobble.clunk.ast.typed;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.RecordType;
import org.zwobble.clunk.util.P;

import java.util.List;

public record TypedRecordNode(
    String name,
    RecordType type,
    List<TypedRecordFieldNode> fields,
    List<TypedTypeLevelExpressionNode> supertypes,
    List<TypedRecordBodyDeclarationNode> body,
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
        return new Builder(name, new RecordType(namespaceName, name), P.vector(), P.vector(), P.vector(), NullSource.INSTANCE);
    }

    public static record Builder(
        String name,
        RecordType type,
        PVector<TypedRecordFieldNode> fields,
        PVector<TypedTypeLevelExpressionNode> supertypes,
        PVector<TypedRecordBodyDeclarationNode> body,
        Source source
    ) {
        public TypedRecordNode build() {
            return new TypedRecordNode(name, type, fields, supertypes, body, source);
        }

        public Builder addField(TypedRecordFieldNode field) {
            return new Builder(name, type, fields.plus(field), supertypes, body, source);
        }

        public Builder addSupertype(TypedTypeLevelExpressionNode supertype) {
            return new Builder(name, type, fields, supertypes.plus(supertype), body, source);
        }

        public Builder addProperty(TypedPropertyNode property) {
            return new Builder(name, type, fields, supertypes, body.plus(property), source);
        }
    }
}
