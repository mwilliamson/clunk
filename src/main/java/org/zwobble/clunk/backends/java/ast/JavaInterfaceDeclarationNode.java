package org.zwobble.clunk.backends.java.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;
import java.util.Optional;

public record JavaInterfaceDeclarationNode(
    List<String> typeParams,
    String name,
    Optional<List<? extends JavaTypeExpressionNode>> permits,
    List<? extends JavaInterfaceMemberDeclarationNode> body
) implements JavaInterfaceMemberDeclarationNode, JavaTypeDeclarationNode {

    @Override
    public <T> T accept(JavaInterfaceMemberDeclarationNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(JavaTypeDeclarationNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder(P.vector(), "X", Optional.empty(), P.vector());
    }

    public record Builder(
        PVector<String> typeParams,
        String name,
        Optional<List<? extends JavaTypeExpressionNode>> permits,
        PVector<JavaInterfaceMemberDeclarationNode> body
    ) {
        public JavaInterfaceDeclarationNode build() {
            return new JavaInterfaceDeclarationNode(typeParams, name, permits, body);
        }

        public Builder name(String name) {
            return new JavaInterfaceDeclarationNode.Builder(typeParams, name, permits, body);
        }

        public Builder sealed(List<JavaTypeExpressionNode> permits) {
            return new JavaInterfaceDeclarationNode.Builder(typeParams, name, Optional.of(permits), body);
        }

        public Builder addMemberDeclaration(JavaInterfaceMemberDeclarationNode declaration) {
            return new Builder(typeParams, name, permits, body.plus(declaration));
        }

        public Builder addTypeParam(String typeParam) {
            return new Builder(typeParams.plus(typeParam), name, permits, body);
        }
    }
}
