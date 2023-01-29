package org.zwobble.clunk.backends.typescript.ast;

public record TypeScriptImportNamedMemberNode(
    String exportName,
    String importName
) implements TypeScriptNode {
}
