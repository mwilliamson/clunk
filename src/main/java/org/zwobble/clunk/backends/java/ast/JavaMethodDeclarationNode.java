package org.zwobble.clunk.backends.java.ast;

import java.util.ArrayList;
import java.util.List;

public record JavaMethodDeclarationNode(
    boolean isStatic,
    JavaTypeExpressionNode returnType,
    String name,
    List<JavaParamNode> params
) implements JavaClassBodyDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder(false, Java.typeReference("void"), "f", List.of());
    }

    public record Builder(
        boolean isStatic,
        JavaTypeExpressionNode returnType,
        String name,
        List<JavaParamNode> params
    ) {
        public JavaMethodDeclarationNode build() {
            return new JavaMethodDeclarationNode(isStatic, returnType, name, params);
        }

        public Builder isStatic(boolean isStatic) {
            return new Builder(isStatic, returnType, name, params);
        }

        public Builder returnType(JavaTypeExpressionNode returnType) {
            return new Builder(isStatic, returnType, name, params);
        }

        public Builder name(String name) {
            return new Builder(isStatic, returnType, name, params);
        }

        public Builder addParam(JavaParamNode param) {
            var params = new ArrayList<>(this.params);
            params.add(param);
            return new Builder(isStatic, returnType, name, params);
        }
    }
}
