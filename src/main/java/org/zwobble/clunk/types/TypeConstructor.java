package org.zwobble.clunk.types;

import java.util.List;

public record TypeConstructor(
    String name,
    List<TypeParameter> params,
    Type genericType
) implements TypeLevelValue {
    @Override
    public String describe() {
        return name;
    }
}
