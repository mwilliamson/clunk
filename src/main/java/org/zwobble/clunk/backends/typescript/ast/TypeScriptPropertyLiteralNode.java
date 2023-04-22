package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptPropertyLiteralNode(
    String name,
    TypeScriptExpressionNode expression
) implements TypeScriptNode {
}
