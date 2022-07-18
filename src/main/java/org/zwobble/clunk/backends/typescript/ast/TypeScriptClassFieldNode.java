package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptClassFieldNode(
    String name,
    TypeScriptExpressionNode type
) implements TypeScriptNode {
}
