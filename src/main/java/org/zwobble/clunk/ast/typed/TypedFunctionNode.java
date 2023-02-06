package org.zwobble.clunk.ast.typed;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.util.P;

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

    public List<TypedParamNode> namedParams() {
        return params.named();
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
        return new Builder(
            "f",
            P.vector(),
            P.vector(),
            Typed.typeLevelReference("Int", IntType.INSTANCE),
            P.vector()
        );
    }

    public record Builder(
        String name,
        PVector<TypedParamNode> positionalParams,
        PVector<TypedParamNode> namedParams,
        TypedTypeLevelExpressionNode returnType,
        PVector<TypedFunctionStatementNode> body
    ) {
        public TypedFunctionNode build() {
            return new TypedFunctionNode(
                name,
                new TypedParamsNode(positionalParams, namedParams, NullSource.INSTANCE),
                returnType,
                body,
                NullSource.INSTANCE
            );
        }

        public Builder addParam(TypedParamNode param) {
            return new Builder(name, positionalParams.plus(param), namedParams, returnType, body);
        }

        public Builder name(String name) {
            return new Builder(name, positionalParams, namedParams, returnType, body);
        }

        public Builder returnType(TypedTypeLevelExpressionNode returnType) {
            return new Builder(name, positionalParams, namedParams, returnType, body);
        }

        public Builder addBodyStatement(TypedFunctionStatementNode statement) {
            return new Builder(name, positionalParams, namedParams, returnType, body.plus(statement));
        }
    }
}
