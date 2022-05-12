package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptImportNode(String module, List<String> exports) implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
