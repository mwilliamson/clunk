package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptConditionalBranchNode(
    TypeScriptExpressionNode condition,
    List<TypeScriptStatementNode> body
) implements TypeScriptNode {
}
