package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptParamNode(
    String name,
    TypeScriptExpressionNode type
) implements TypeScriptNode {
}
