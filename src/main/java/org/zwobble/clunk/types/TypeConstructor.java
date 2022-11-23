package org.zwobble.clunk.types;

import java.util.List;

public record TypeConstructor(
    List<TypeParameter> params,
    StructuredType genericType
) implements TypeLevelValue {
    public NamespaceName namespaceName() {
        return genericType.namespaceName();
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
