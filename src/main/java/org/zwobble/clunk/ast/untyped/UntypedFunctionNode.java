package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;

import java.util.ArrayList;
import java.util.List;

public record UntypedFunctionNode(
    String name,
    List<UntypedParamNode> params,
    UntypedTypeLevelExpressionNode returnType,
    List<UntypedFunctionStatementNode> body,
    Source source
) implements UntypedNamespaceStatementNode, UntypedRecordBodyDeclarationNode {
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
            List.of(),
            Untyped.typeLevelReference("Unit"),
            List.of(),
            NullSource.INSTANCE
        );
    }

    public static record Builder(
        String name,
        List<UntypedParamNode> params,
        UntypedTypeLevelExpressionNode returnType,
        List<UntypedFunctionStatementNode> body,
        Source source
    ) {
        public UntypedFunctionNode build() {
            return new UntypedFunctionNode(name, params, returnType, body, source);
        }

        public Builder addParam(UntypedParamNode param) {
            var params = new ArrayList<>(this.params);
            params.add(param);
            return new Builder(name, params, returnType, body, source);
        }

        public Builder name(String name) {
            return new Builder(name, params, returnType, body, source);
        }

        public Builder returnType(UntypedTypeLevelExpressionNode returnType) {
            return new Builder(name, params, returnType, body, source);
        }

        public Builder addBodyStatement(UntypedFunctionStatementNode statement) {
            var body = new ArrayList<>(this.body);
            body.add(statement);
            return new Builder(name, params, returnType, body, source);
        }
    }

}
