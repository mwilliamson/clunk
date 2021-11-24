package org.zwobble.clunk.backends.typescript.ast;

import java.util.ArrayList;
import java.util.List;

public record TypeScriptInterfaceDeclarationNode(
    String name,
    List<TypeScriptInterfaceFieldNode> fields
) implements TypeScriptNode {
    public static Builder builder(String name) {
        return new Builder(name, List.of());
    }

    public static record Builder(String name, List<TypeScriptInterfaceFieldNode> fields) {
        public TypeScriptInterfaceDeclarationNode build() {
            return new TypeScriptInterfaceDeclarationNode(name, fields);
        }

        public Builder addField(TypeScriptInterfaceFieldNode field) {
            var components = new ArrayList<>(this.fields);
            components.add(field);
            return new TypeScriptInterfaceDeclarationNode.Builder(name, components);
        }
    }
}
