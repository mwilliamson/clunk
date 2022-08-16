package org.zwobble.clunk.ast.typed;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.util.P;

import java.util.List;
import java.util.Optional;

public record TypedSwitchNode(
    TypedReferenceNode expression,
    List<TypedSwitchCaseNode> cases,
    Optional<Type> returnType,
    Source source
) implements TypedFunctionStatementNode {

    public boolean returns() {
        return returnType.isPresent();
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(TypedReferenceNode expression) {
        return new Builder(expression, P.vector(), Optional.empty(), NullSource.INSTANCE);
    }

    public record Builder(
        TypedReferenceNode expression,
        PVector<TypedSwitchCaseNode> cases,
        Optional<Type> returnType,
        Source source
    ) {
        public TypedSwitchNode build() {
            return new TypedSwitchNode(expression, cases, returnType, source);
        }

        public Builder addCase(TypedSwitchCaseNode switchCase) {
            return new Builder(expression, cases.plus(switchCase), returnType, source);
        }

        public Builder returnType(Type returnType) {
            return new Builder(expression, cases, Optional.of(returnType), source);
        }

        public Builder neverReturns() {
            return new Builder(expression, cases, Optional.empty(), source);
        }
    }
}
