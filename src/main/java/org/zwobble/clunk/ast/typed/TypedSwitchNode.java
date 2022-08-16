package org.zwobble.clunk.ast.typed;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.util.P;

import java.util.List;

public record TypedSwitchNode(
    TypedReferenceNode expression,
    List<TypedSwitchCaseNode> cases,
    boolean returns,
    Source source
) implements TypedFunctionStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(TypedReferenceNode expression) {
        return new Builder(expression, P.vector(), false, NullSource.INSTANCE);
    }

    public record Builder(
        TypedReferenceNode expression,
        PVector<TypedSwitchCaseNode> cases,
        boolean returns,
        Source source
    ) {
        public TypedSwitchNode build() {
            return new TypedSwitchNode(expression, cases, returns, source);
        }

        public Builder addCase(TypedSwitchCaseNode switchCase) {
            return new Builder(expression, cases.plus(switchCase), returns, source);
        }

        public Builder returns(boolean returns) {
            return new Builder(expression, cases, returns, source);
        }
    }
}
