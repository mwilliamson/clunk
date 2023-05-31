package org.zwobble.clunk.ast.typed;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.InterfaceType;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.util.P;

import java.util.List;

public record TypedInterfaceNode(
    String name,
    InterfaceType type,
    List<? extends TypedInterfaceBodyDeclarationNode> body,
    Source source
) implements TypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(NamespaceId namespaceId, String name) {
        return new Builder(
            name,
            new InterfaceType(namespaceId, name, false),
            P.vector(),
            NullSource.INSTANCE
        );
    }

    public record Builder(
        String name,
        InterfaceType type,
        PVector<TypedInterfaceBodyDeclarationNode> body,
        Source source
    ) {
        public TypedInterfaceNode build() {
            return new TypedInterfaceNode(name, type, body, source);
        }

        public Builder addMethod(TypedFunctionSignatureNode method) {
            return new Builder(name, type, body.plus(method), source);
        }
    }
}
