package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.IntType;

import java.util.ArrayList;
import java.util.List;

public record TypedFunctionNode(
    String name,
    TypedParamsNode params,
    TypedTypeLevelExpressionNode returnType,
    List<TypedFunctionStatementNode> body,
    Source source
) implements TypedNamespaceStatementNode, TypedRecordBodyDeclarationNode {
    public List<TypedParamNode> positionalParams() {
        return params.positional();
    }

    @Override
    public <T> T accept(TypedNamespaceStatementNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(TypedRecordBodyDeclarationNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder("f", List.of(), Typed.typeLevelReference("Int", IntType.INSTANCE), List.of());
    }

    public static record Builder(
        String name,
        List<TypedParamNode> params,
        TypedTypeLevelExpressionNode returnType,
        List<TypedFunctionStatementNode> body
    ) {
        public TypedFunctionNode build() {
            return new TypedFunctionNode(
                name,
                new TypedParamsNode(params, NullSource.INSTANCE),
                returnType,
                body,
                NullSource.INSTANCE
            );
        }

        public Builder addParam(TypedParamNode param) {
            var params = new ArrayList<>(this.params);
            params.add(param);
            return new Builder(name, params, returnType, body);
        }

        public Builder name(String name) {
            return new Builder(name, params, returnType, body);
        }

        public Builder returnType(TypedTypeLevelExpressionNode returnType) {
            return new Builder(name, params, returnType, body);
        }

        public Builder addBodyStatement(TypedFunctionStatementNode statement) {
            var body = new ArrayList<>(this.body);
            body.add(statement);
            return new Builder(name, params, returnType, body);
        }
    }
}
