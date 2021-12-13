package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.Type;

import java.util.ArrayList;
import java.util.List;

public record TypedFunctionNode(
    String name,
    List<TypedParamNode> params,
    TypedStaticExpressionNode returnType,
    Source source
) implements TypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }


    public static Builder builder() {
        return new Builder("f", List.of(), Typed.staticExpression(IntType.INSTANCE));
    }

    public static record Builder(
        String name,
        List<TypedParamNode> params,
        TypedStaticExpressionNode returnType
    ) {
        public TypedFunctionNode build() {
            return new TypedFunctionNode(name, params, returnType, NullSource.INSTANCE);
        }

        public Builder addParam(TypedParamNode param) {
            var params = new ArrayList<>(this.params);
            params.add(param);
            return new Builder(name, params, returnType);
        }

        public Builder name(String name) {
            return new Builder(name, params, returnType);
        }

        public Builder returnType(Type returnType) {
            return new Builder(name, params, Typed.staticExpression(returnType));
        }
    }
}
