package org.zwobble.clunk.types;

import java.util.HashMap;

public record TypeLevelValueType(TypeLevelValue value) implements Type {
    @Override
    public String describe() {
        return "TypeLevelValue[" + value.describe() + "]";
    }

    @Override
    public Type replace(HashMap<TypeParameter, Type> typeMap) {
        // TODO: implement properly
        return this;
    }
}
