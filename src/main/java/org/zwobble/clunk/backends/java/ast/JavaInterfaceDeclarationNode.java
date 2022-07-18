package org.zwobble.clunk.backends.java.ast;

import java.util.List;
import java.util.Optional;

public record JavaInterfaceDeclarationNode(
    String name,
    Optional<List<? extends JavaTypeExpressionNode>> permits
) implements JavaTypeDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("X", Optional.empty());
    }

    public static record Builder(
        String name,
        Optional<List<? extends JavaTypeExpressionNode>> permits
    ) {
        public JavaInterfaceDeclarationNode build() {
            return new JavaInterfaceDeclarationNode(name, permits);
        }

        public Builder name(String name) {
            return new JavaInterfaceDeclarationNode.Builder(name, permits);
        }

        public Builder sealed(List<JavaTypeExpressionNode> permits) {
            return new JavaInterfaceDeclarationNode.Builder(name, Optional.of(permits));
        }
    }
}
