package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptInterfaceFieldNode(
    String name,
    TypeScriptReferenceNode type
) implements TypeScriptNode {
}
