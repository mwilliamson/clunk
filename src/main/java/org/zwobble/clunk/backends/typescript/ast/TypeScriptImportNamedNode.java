package org.zwobble.clunk.backends.typescript.ast;

import java.util.List;

public record TypeScriptImportNamedNode(
    String module,
    List<TypeScriptImportNamedMemberNode> members
) implements TypeScriptStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
