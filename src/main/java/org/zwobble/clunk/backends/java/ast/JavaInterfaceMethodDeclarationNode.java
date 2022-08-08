package org.zwobble.clunk.backends.java.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;

public record JavaInterfaceMethodDeclarationNode(
    List<String> typeParams,
    JavaTypeExpressionNode returnType,
    String name,
    List<JavaParamNode> params
) implements JavaInterfaceMemberDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder(P.vector(), Java.typeVariableReference("void"), "f", P.vector());
    }

    public record Builder(
        PVector<String> typeParams,
        JavaTypeExpressionNode returnType,
        String name,
        PVector<JavaParamNode> params
    ) {
        public JavaInterfaceMethodDeclarationNode build() {
            return new JavaInterfaceMethodDeclarationNode(typeParams, returnType, name, params);
        }

        public Builder addTypeParam(String typeParamName) {
            return new Builder(typeParams.plus(typeParamName), returnType, name, params);
        }

        public Builder returnType(JavaTypeExpressionNode returnType) {
            return new Builder(typeParams, returnType, name, params);
        }

        public Builder name(String name) {
            return new Builder(typeParams, returnType, name, params);
        }

        public Builder addParam(JavaParamNode param) {
            return new Builder(typeParams, returnType, name, params.plus(param));
        }
    }
}
