package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptExportNode(
    List<String> names
) implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
