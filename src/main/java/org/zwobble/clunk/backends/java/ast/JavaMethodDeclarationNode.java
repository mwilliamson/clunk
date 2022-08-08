package org.zwobble.clunk.backends.java.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.ArrayList;
import java.util.List;

public record JavaMethodDeclarationNode(
    List<JavaAnnotationNode> annotations,
    boolean isStatic,
    List<String> typeParams,
    JavaTypeExpressionNode returnType,
    String name,
    List<JavaParamNode> params,
    List<JavaStatementNode> body
) implements JavaClassBodyDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder(List.of(), false, P.vector(), Java.typeVariableReference("void"), "f", List.of(), List.of());
    }

    public record Builder(
        List<JavaAnnotationNode> annotations,
        boolean isStatic,
        PVector<String> typeParams,
        JavaTypeExpressionNode returnType,
        String name,
        List<JavaParamNode> params,
        List<JavaStatementNode> body
    ) {
        public JavaMethodDeclarationNode build() {
            return new JavaMethodDeclarationNode(annotations, isStatic, typeParams, returnType, name, params, body);
        }

        public Builder isStatic(boolean isStatic) {
            return new Builder(annotations, isStatic, typeParams, returnType, name, params, body);
        }

        public Builder addTypeParam(String typeParam) {
            return new Builder(annotations, isStatic, typeParams.plus(typeParam), returnType, name, params, body);
        }

        public Builder returnType(JavaTypeExpressionNode returnType) {
            return new Builder(annotations, isStatic, typeParams, returnType, name, params, body);
        }

        public Builder name(String name) {
            return new Builder(annotations, isStatic, typeParams, returnType, name, params, body);
        }

        public Builder addAnnotation(JavaAnnotationNode annotation) {
            var annotations = new ArrayList<>(this.annotations);
            annotations.add(annotation);
            return new Builder(annotations, isStatic, typeParams, returnType, name, params, body);
        }

        public Builder addParam(JavaParamNode param) {
            var params = new ArrayList<>(this.params);
            params.add(param);
            return new Builder(annotations, isStatic, typeParams, returnType, name, params, body);
        }

        public Builder addBodyStatement(JavaStatementNode statement) {
            var body = new ArrayList<>(this.body);
            body.add(statement);
            return new Builder(annotations, isStatic, typeParams, returnType, name, params, body);
        }
    }
}
