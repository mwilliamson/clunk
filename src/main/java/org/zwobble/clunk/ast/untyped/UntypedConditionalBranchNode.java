package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedConditionalBranchNode(
    UntypedExpressionNode condition,
    List<UntypedFunctionStatementNode> body,
    Source source
) implements UntypedNode {
}
