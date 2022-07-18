package org.zwobble.clunk.backends.java.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;

public record JavaRecordDeclarationNode(
    String name,
    List<JavaRecordComponentNode> components,
    List<? extends JavaTypeExpressionNode> implements_
) implements JavaTypeDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(String name) {
        return new Builder(name, P.vector(), P.vector());
    }

    public static record Builder(
        String name,
        PVector<JavaRecordComponentNode> components,
        PVector<JavaTypeExpressionNode> implements_
    ) {
        public JavaRecordDeclarationNode build() {
            return new JavaRecordDeclarationNode(name, components, implements_);
        }

        public Builder addComponent(JavaRecordComponentNode component) {
            return new Builder(name, components.plus(component), implements_);
        }

        public Builder addImplements(JavaTypeExpressionNode implementsType) {
            return new Builder(name, components, implements_.plus(implementsType));
        }
    }
}
