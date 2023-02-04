package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

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
}
