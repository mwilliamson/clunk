package org.zwobble.clunk.ast.untyped;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.util.P;

public record UntypedFunctionSignatureNode(
    String name,
    UntypedParamsNode params,
    UntypedTypeLevelExpressionNode returnType,
    Source source
) implements UntypedInterfaceBodyDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder() {
        return new Builder(
            "f",
            P.vector(),
            P.vector(),
            Untyped.typeLevelReference("Unit"),
            NullSource.INSTANCE
        );
    }

    public record Builder(
        String name,
        PVector<UntypedParamNode> positionalParams,
        PVector<UntypedParamNode> namedParams,
        UntypedTypeLevelExpressionNode returnType,
        Source source
    ) {
        public UntypedFunctionSignatureNode build() {
            return new UntypedFunctionSignatureNode(
                name,
                new UntypedParamsNode(positionalParams, namedParams, source),
                returnType,
                source
            );
        }

        public Builder addPositionalParam(UntypedParamNode param) {
            return new Builder(name, positionalParams.plus(param), namedParams, returnType, source);
        }

        public Builder addNamedParam(UntypedParamNode param) {
            return new Builder(name, positionalParams, namedParams.plus(param), returnType, source);
        }

        public Builder name(String name) {
            return new Builder(name, positionalParams, namedParams, returnType, source);
        }

        public Builder returnType(UntypedTypeLevelExpressionNode returnType) {
            return new Builder(name, positionalParams, namedParams, returnType, source);
        }
    }
}
