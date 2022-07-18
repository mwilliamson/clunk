package org.zwobble.clunk.backends.typescript.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;

public record TypeScriptClassDeclarationNode(
    String name,
    List<TypeScriptClassFieldNode> fields
) implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(String name) {
        return new Builder(name, P.vector());
    }

    public static record Builder(String name, PVector<TypeScriptClassFieldNode> fields) {
        public TypeScriptClassDeclarationNode build() {
            return new TypeScriptClassDeclarationNode(name, fields);
        }

        public Builder addField(TypeScriptClassFieldNode field) {
            return new TypeScriptClassDeclarationNode.Builder(name, fields.plus(field));
        }
    }
}
