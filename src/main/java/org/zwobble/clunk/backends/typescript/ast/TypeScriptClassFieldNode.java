package org.zwobble.clunk.backends.typescript.ast;

import java.util.Optional;

public record TypeScriptClassFieldNode(
    String name,
    TypeScriptExpressionNode type,
    Optional<TypeScriptExpressionNode> constantValue
) implements TypeScriptNode {
}
