package org.zwobble.clunk.backends.java.ast;

import java.util.ArrayList;
import java.util.List;

public record JavaClassDeclarationNode(
    String name,
    List<JavaClassBodyDeclarationNode> body
) implements JavaTypeDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("X", List.of());
    }

    public static record Builder(
        String name,
        List<JavaClassBodyDeclarationNode> body
    ) {
        public JavaClassDeclarationNode build() {
            return new JavaClassDeclarationNode(name, body);
        }

        public Builder addBodyDeclaration(JavaClassBodyDeclarationNode bodyDeclaration) {
            var body = new ArrayList<>(this.body);
            body.add(bodyDeclaration);
            return new Builder(name, body);
        }

        public Builder name(String name) {
            return new Builder(name, body);
        }
    }
}
