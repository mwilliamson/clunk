package org.zwobble.clunk.ast.typed;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.InterfaceType;

import java.util.List;

public record TypedInterfaceNode(
    String name,
    InterfaceType type,
    List<TypedInterfaceBodyDeclarationNode> body,
    Source source
) implements TypedNamespaceStatementNode {
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
