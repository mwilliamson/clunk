package org.zwobble.clunk.ast.typed;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.FunctionType;
import org.zwobble.clunk.types.Types;
import org.zwobble.clunk.util.P;

import java.util.List;

public record TypedFunctionSignatureNode(
    String name,
    TypedParamsNode params,
    TypedTypeLevelExpressionNode returnType,
    FunctionType type,
    Source source
) implements TypedInterfaceBodyDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder(
            "f",
            P.vector(),
            P.vector(),
            Typed.typeLevelReference("Int", Types.INT),
            Types.functionType(List.of(), Types.INT)
        );
    }

    public record Builder(
        String name,
        PVector<TypedParamNode> positionalParams,
        PVector<TypedParamNode> namedParams,
        TypedTypeLevelExpressionNode returnType,
        FunctionType type
    ) {
        public TypedFunctionSignatureNode build() {
            return new TypedFunctionSignatureNode(
                name,
                new TypedParamsNode(positionalParams, namedParams, NullSource.INSTANCE),
                returnType,
                type,
                NullSource.INSTANCE
            );
        }

        public Builder addPositionalParam(TypedParamNode param) {
            return new Builder(name, positionalParams.plus(param), namedParams, returnType, type);
        }

        public Builder addNamedParam(TypedParamNode param) {
            return new Builder(name, positionalParams, namedParams.plus(param), returnType, type);
        }

        public Builder name(String name) {
            return new Builder(name, positionalParams, namedParams, returnType, type);
        }

        public Builder returnType(TypedTypeLevelExpressionNode returnType) {
            return new Builder(name, positionalParams, namedParams, returnType, type);
        }
    }
}
