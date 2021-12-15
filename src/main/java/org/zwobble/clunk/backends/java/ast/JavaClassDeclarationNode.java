package org.zwobble.clunk.backends.java.ast;

public record JavaClassDeclarationNode(String name) implements JavaTypeDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("X");
    }

    public static record Builder(String name) {
        public JavaClassDeclarationNode build() {
            return new JavaClassDeclarationNode(name);
        }

        public Builder name(String name) {
            return new Builder(name);
        }
    }
}
