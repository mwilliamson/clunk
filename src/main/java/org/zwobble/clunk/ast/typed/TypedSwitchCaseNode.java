package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;

import java.util.List;

public record TypedSwitchCaseNode(
    TypedTypeLevelExpressionNode type,
    List<TypedFunctionStatementNode> body,
    Source source
) implements TypedNode {
}
