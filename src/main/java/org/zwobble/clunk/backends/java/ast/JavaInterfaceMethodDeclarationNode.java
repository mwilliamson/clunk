package org.zwobble.clunk.backends.java.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;

public record JavaInterfaceMethodDeclarationNode(
    JavaTypeExpressionNode returnType,
    String name,
    List<JavaParamNode> params
) implements JavaInterfaceMemberDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder(Java.typeVariableReference("void"), "f", P.vector());
    }

    public record Builder(
        JavaTypeExpressionNode returnType,
        String name,
        PVector<JavaParamNode> params
    ) {
        public JavaInterfaceMethodDeclarationNode build() {
            return new JavaInterfaceMethodDeclarationNode(returnType, name, params);
        }

        public Builder returnType(JavaTypeExpressionNode returnType) {
            return new Builder(returnType, name, params);
        }

        public Builder name(String name) {
            return new Builder(returnType, name, params);
        }

        public Builder addParam(JavaParamNode param) {
            return new Builder(returnType, name, params.plus(param));
        }
    }
}
