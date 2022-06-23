package org.zwobble.clunk.backends.java.ast;

public record JavaInterfaceDeclarationNode(
    String name
) implements JavaTypeDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("X");
    }

    public static record Builder(
        String name
    ) {
        public JavaInterfaceDeclarationNode build() {
            return new JavaInterfaceDeclarationNode(name);
        }

        public JavaInterfaceDeclarationNode.Builder name(String name) {
            return new JavaInterfaceDeclarationNode.Builder(name);
        }
    }
}
