package org.zwobble.clunk.types;

import java.util.HashMap;

public record TypeParameter(String name, Variance variance) implements Type {
    public static TypeParameter covariant(String name) {
        return new TypeParameter(name, Variance.COVARIANT);
    }

    public static TypeParameter invariant(String name) {
        return new TypeParameter(name, Variance.INVARIANT);
    }

    @Override
    public String describe() {
        return name;
    }

    @Override
    public Type replace(HashMap<TypeParameter, Type> typeMap) {
        return typeMap.getOrDefault(this, this);
    }
}
