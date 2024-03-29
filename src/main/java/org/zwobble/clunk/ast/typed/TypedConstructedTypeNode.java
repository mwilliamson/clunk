package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.TypeLevelValue;
import org.zwobble.clunk.types.Variance;

import java.util.List;

public record TypedConstructedTypeNode(
    TypedTypeLevelExpressionNode receiver,
    List<Arg> args,
    TypeLevelValue value,
    Source source
) implements TypedTypeLevelExpressionNode {

    public record Arg(TypedTypeLevelExpressionNode type, Variance variance) {
        public static Arg invariant(TypedTypeLevelExpressionNode type) {
            return new Arg(type, Variance.INVARIANT);
        }

        public static Arg covariant(TypedTypeLevelExpressionNode type) {
            return new Arg(type, Variance.COVARIANT);
        }

        public boolean isCovariant() {
            return variance == Variance.COVARIANT;
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
