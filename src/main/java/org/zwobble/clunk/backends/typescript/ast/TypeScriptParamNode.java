package org.zwobble.clunk.backends.typescript.ast;

import java.util.Optional;

public record TypeScriptParamNode(
    String name,
    Optional<TypeScriptExpressionNode> type
) implements TypeScriptNode {
}
