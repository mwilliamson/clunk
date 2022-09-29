package org.zwobble.clunk.backends.java.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;

public record JavaClassDeclarationNode(
    String name,
    List<JavaClassBodyDeclarationNode> body
) implements JavaClassBodyDeclarationNode, JavaTypeDeclarationNode {
    @Override
    public <T> T accept(JavaClassBodyDeclarationNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(JavaTypeDeclarationNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("X", P.vector());
    }

    public static record Builder(
        String name,
        PVector<JavaClassBodyDeclarationNode> body
    ) {
        public JavaClassDeclarationNode build() {
            return new JavaClassDeclarationNode(name, body);
        }

        public Builder addBodyDeclaration(JavaClassBodyDeclarationNode bodyDeclaration) {
            return new Builder(name, body.plus(bodyDeclaration));
        }

        public Builder name(String name) {
            return new Builder(name, body);
        }
    }
}
