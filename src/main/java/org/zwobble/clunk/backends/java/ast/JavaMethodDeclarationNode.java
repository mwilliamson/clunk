package org.zwobble.clunk.backends.java.ast;

public record JavaMethodDeclarationNode(
    JavaTypeExpressionNode returnType,
    String name
) implements JavaClassBodyDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder(Java.typeReference("void"), "f");
    }

    public record Builder(
        JavaTypeExpressionNode returnType,
        String name
    ) {
        public JavaMethodDeclarationNode build() {
            return new JavaMethodDeclarationNode(returnType, name);
        }

        public Builder name(String name) {
            return new Builder(returnType, name);
        }

        public Builder returnType(JavaTypeExpressionNode returnType) {
            return new Builder(returnType, name);
        }
    }
}
