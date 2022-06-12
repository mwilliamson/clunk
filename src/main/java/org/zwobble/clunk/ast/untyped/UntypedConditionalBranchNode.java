package org.zwobble.clunk.ast.untyped;

import java.util.List;

public record UntypedConditionalBranchNode(
    UntypedExpressionNode condition,
    List<UntypedFunctionStatementNode> body
) {
}
