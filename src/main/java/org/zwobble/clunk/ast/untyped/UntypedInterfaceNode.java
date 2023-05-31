package org.zwobble.clunk.ast.untyped;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.util.P;

import java.util.List;

public record UntypedInterfaceNode(
    String name,
    boolean isSealed,
    List<UntypedInterfaceBodyDeclarationNode> body,
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
        return new Builder(name, false, P.vector(), NullSource.INSTANCE);
    }

    public record Builder(
        String name,
        boolean isSealed,
        PVector<UntypedInterfaceBodyDeclarationNode> body,
        Source source
    ) {
        public UntypedInterfaceNode build() {
            return new UntypedInterfaceNode(name, isSealed, body, source);
        }

        public Builder addBodyDeclaration(UntypedInterfaceBodyDeclarationNode declaration) {
            return new Builder(name, isSealed, body.plus(declaration), source);
        }
    }
}
