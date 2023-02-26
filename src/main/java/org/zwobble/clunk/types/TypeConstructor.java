package org.zwobble.clunk.types;

import java.util.List;

public record TypeConstructor(
    List<TypeParameter> params,
    StructuredType genericType
) implements TypeLevelValue {
    public NamespaceId namespaceId() {
        return genericType.namespaceId();
    }

    public String name() {
        return genericType.name();
    }

    @Override
    public String describe() {
        return name();
    }

    public TypeParameter param(int index) {
        return params.get(index);
    }
}
