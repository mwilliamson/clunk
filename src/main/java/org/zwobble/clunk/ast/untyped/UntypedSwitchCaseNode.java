package org.zwobble.clunk.ast.untyped;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record UntypedSwitchCaseNode(
    UntypedTypeLevelExpressionNode type,
    String variableName,
    List<UntypedFunctionStatementNode> body,
    Source source
) implements UntypedNode {
}
