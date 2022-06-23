package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptInterfaceFieldNode(
    String name,
    TypeScriptExpressionNode type
) implements TypeScriptNode {
}
