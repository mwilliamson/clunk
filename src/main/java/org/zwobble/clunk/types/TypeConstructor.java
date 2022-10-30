package org.zwobble.clunk.types;

import java.util.List;

public record TypeConstructor(
    String name,
    List<TypeParameter> params,
    StructuredType genericType
) implements TypeLevelValue {
    @Override
    public String describe() {
        return name;
    }
}
