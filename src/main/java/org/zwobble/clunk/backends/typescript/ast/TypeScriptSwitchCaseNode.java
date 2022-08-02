package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptSwitchCaseNode(
    TypeScriptExpressionNode expression,
    List<TypeScriptStatementNode> body
) implements TypeScriptNode {
}
