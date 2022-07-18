package org.zwobble.clunk.backends.typescript.ast;

import org.pcollections.PVector;
import org.zwobble.clunk.util.P;

import java.util.List;

public record TypeScriptGetterNode(
    String name,
    TypeScriptExpressionNode type,
    List<TypeScriptStatementNode> body
) implements TypeScriptClassBodyDeclarationNode {
    @Override
    public <T> T accept(TypeScriptClassBodyDeclarationNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("f", TypeScript.reference("void"), P.vector());
    }

    public static record Builder(
        String name,
        TypeScriptExpressionNode type,
        PVector<TypeScriptStatementNode> body
    ) {
        public TypeScriptGetterNode build() {
            return new TypeScriptGetterNode(name, type, body);
        }

        public Builder name(String name) {
            return new Builder(name, type, body);
        }

        public Builder returnType(TypeScriptExpressionNode returnType) {
            return new Builder(name, returnType, body);
        }

        public Builder addBodyStatement(TypeScriptStatementNode statement) {
            return new Builder(name, type, body.plus(statement));
        }
    }
}
