package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptModuleNode(
    String path,
    List<TypeScriptStatementNode> statements
) implements TypeScriptNode {
}
