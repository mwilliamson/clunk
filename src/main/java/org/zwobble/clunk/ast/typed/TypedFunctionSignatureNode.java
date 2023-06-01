package org.zwobble.clunk.ast.typed;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.FunctionSignature;
import org.zwobble.clunk.types.ParamTypes;
import org.zwobble.clunk.types.Types;
import org.zwobble.clunk.util.P;

public record TypedFunctionSignatureNode(
    String name,
    TypedParamsNode params,
    TypedTypeLevelExpressionNode returnType,
    FunctionSignature signature,
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
            new FunctionSignature(ParamTypes.empty(), Types.INT)
        );
    }

    public record Builder(
        String name,
        PVector<TypedParamNode> positionalParams,
        PVector<TypedParamNode> namedParams,
        TypedTypeLevelExpressionNode returnType,
        FunctionSignature signature
    ) {
        public TypedFunctionSignatureNode build() {
            return new TypedFunctionSignatureNode(
                name,
                new TypedParamsNode(positionalParams, namedParams, NullSource.INSTANCE),
                returnType,
                signature,
                NullSource.INSTANCE
            );
        }

        public Builder addPositionalParam(TypedParamNode param) {
            return new Builder(name, positionalParams.plus(param), namedParams, returnType, signature);
        }

        public Builder addNamedParam(TypedParamNode param) {
            return new Builder(name, positionalParams, namedParams.plus(param), returnType, signature);
        }

        public Builder name(String name) {
            return new Builder(name, positionalParams, namedParams, returnType, signature);
        }

        public Builder returnType(TypedTypeLevelExpressionNode returnType) {
            return new Builder(name, positionalParams, namedParams, returnType, signature);
        }
    }
}
