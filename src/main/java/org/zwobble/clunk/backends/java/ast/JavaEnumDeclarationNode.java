package org.zwobble.clunk.backends.java.ast;

import java.util.List;

public record JavaEnumDeclarationNode(
    String name,
    List<String> members
) implements JavaTypeDeclarationNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
