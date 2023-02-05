package org.zwobble.clunk.ast.untyped;

import org.pcollections.PVector;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.util.P;

import java.util.List;

public record UntypedCallNode(
    UntypedExpressionNode receiver,
    List<UntypedTypeLevelExpressionNode> typeLevelArgs,
    UntypedArgsNode args,
    Source source
) implements UntypedExpressionNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    public List<UntypedExpressionNode> positionalArgs() {
        return args.positional();
    }

    public List<UntypedNamedArgNode> namedArgs() {
        return args.named();
    }

    public static Builder builder(UntypedExpressionNode receiver) {
        return new Builder(receiver, P.vector(), P.vector(), P.vector());
    }

    public record Builder(
        UntypedExpressionNode receiver,
        PVector<UntypedTypeLevelExpressionNode> typeLevelArgs,
        PVector<UntypedExpressionNode> positionalArgs,
        PVector<UntypedNamedArgNode> namedArgs
    ) {
        public UntypedCallNode build() {
            return new UntypedCallNode(
                receiver,
                typeLevelArgs,
                new UntypedArgsNode(positionalArgs, namedArgs, NullSource.INSTANCE),
                NullSource.INSTANCE
            );
        }

        public Builder addNamedArg(String name, UntypedExpressionNode expression) {
            return new Builder(
                receiver,
                typeLevelArgs,
                positionalArgs,
                namedArgs.plus(Untyped.namedArg(name, expression))
            );
        }
    }
}
