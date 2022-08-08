package org.zwobble.clunk.backends.java.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;
import java.util.Optional;

public record JavaInterfaceDeclarationNode(
    String name,
    Optional<List<? extends JavaTypeExpressionNode>> permits,
    List<JavaInterfaceMemberDeclarationNode> body
) implements JavaTypeDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("X", Optional.empty(), P.vector());
    }

    public record Builder(
        String name,
        Optional<List<? extends JavaTypeExpressionNode>> permits,
        PVector<JavaInterfaceMemberDeclarationNode> body
    ) {
        public JavaInterfaceDeclarationNode build() {
            return new JavaInterfaceDeclarationNode(name, permits, body);
        }

        public Builder name(String name) {
            return new JavaInterfaceDeclarationNode.Builder(name, permits, body);
        }

        public Builder sealed(List<JavaTypeExpressionNode> permits) {
            return new JavaInterfaceDeclarationNode.Builder(name, Optional.of(permits), body);
        }

        public Builder addMemberDeclaration(JavaInterfaceMemberDeclarationNode declaration) {
            return new Builder(name, permits, body.plus(declaration));
        }
    }
}
