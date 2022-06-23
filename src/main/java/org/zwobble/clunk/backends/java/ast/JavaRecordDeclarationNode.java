package org.zwobble.clunk.backends.java.ast;

import java.util.ArrayList;
import java.util.List;

public record JavaRecordDeclarationNode(
    String name,
    List<JavaRecordComponentNode> components,
    List<JavaTypeExpressionNode> implements_
) implements JavaTypeDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(String name) {
        return new Builder(name, List.of(), List.of());
    }

    public static record Builder(
        String name,
        List<JavaRecordComponentNode> components,
        List<JavaTypeExpressionNode> implements_
    ) {
        public JavaRecordDeclarationNode build() {
            return new JavaRecordDeclarationNode(name, components, implements_);
        }

        public Builder addComponent(JavaRecordComponentNode component) {
            var components = new ArrayList<>(this.components);
            components.add(component);
            return new Builder(name, components, implements_);
        }

        public Builder addImplements(JavaTypeExpressionNode implementsType) {
            var implements_ = new ArrayList<>(this.implements_);
            implements_.add(implementsType);
            return new Builder(name, components, implements_);
        }
    }
}
