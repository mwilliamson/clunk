package org.zwobble.clunk.ast.untyped;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.util.P;

import java.util.List;

public record UntypedFunctionNode(
    String name,
    UntypedParamsNode params,
    UntypedTypeLevelExpressionNode returnType,
    List<UntypedFunctionStatementNode> body,
    Source source
) implements UntypedNamespaceStatementNode, UntypedRecordBodyDeclarationNode {
    public List<UntypedParamNode> positionalParams() {
        return params.positional();
    }

    public List<UntypedParamNode> namedParams() {
        return params.named();
    }

    @Override
    public boolean isTypeDefinition() {
        return false;
    }

    @Override
    public <T> T accept(UntypedNamespaceStatementNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <T> T accept(UntypedRecordBodyDeclarationNode.Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static UntypedFunctionNode.Builder builder() {
        return new UntypedFunctionNode.Builder(
            "f",
            P.vector(),
            P.vector(),
            Untyped.typeLevelReference("Unit"),
            P.vector(),
            NullSource.INSTANCE
        );
    }

    public record Builder(
        String name,
        PVector<UntypedParamNode> positionalParams,
        PVector<UntypedParamNode> namedParams,
        UntypedTypeLevelExpressionNode returnType,
        PVector<UntypedFunctionStatementNode> body,
        Source source
    ) {
        public UntypedFunctionNode build() {
            return new UntypedFunctionNode(
                name,
                new UntypedParamsNode(positionalParams, namedParams, source),
                returnType,
                body,
                source
            );
        }

        public Builder addPositionalParam(UntypedParamNode param) {
            return new Builder(name, positionalParams.plus(param), namedParams, returnType, body, source);
        }

        public Builder addNamedParam(UntypedParamNode param) {
            return new Builder(name, positionalParams, namedParams.plus(param), returnType, body, source);
        }

        public Builder name(String name) {
            return new Builder(name, positionalParams, namedParams, returnType, body, source);
        }

        public Builder returnType(UntypedTypeLevelExpressionNode returnType) {
            return new Builder(name, positionalParams, namedParams, returnType, body, source);
        }

        public Builder addBodyStatement(UntypedFunctionStatementNode statement) {
            return new Builder(name, positionalParams, namedParams, returnType, body.plus(statement), source);
        }
    }

}
