package org.zwobble.clunk.backends.java.ast;

import java.util.ArrayList;
import java.util.List;

public record JavaRecordDeclarationNode(String name, List<JavaRecordComponentNode> components) {
    public static Builder builder(String name) {
        return new Builder(name, List.of());
    }

    public static record Builder(String name, List<JavaRecordComponentNode> components) {
        public JavaRecordDeclarationNode build() {
            return new JavaRecordDeclarationNode(name, components);
        }

        public Builder addComponent(JavaRecordComponentNode component) {
            var components = new ArrayList<>(this.components);
            components.add(component);
            return new Builder(name, components);
        }
    }
}
