package org.zwobble.clunk.types;

import java.util.HashMap;

public record TypeParameter(
    NamespaceName namespaceName,
    String typeName,
    String name,
    Variance variance
) implements Type {
    public static TypeParameter covariant(NamespaceName namespaceName, String typeName, String name) {
        return new TypeParameter(namespaceName, typeName, name, Variance.COVARIANT);
    }

    public static TypeParameter invariant(NamespaceName namespaceName, String typeName, String name) {
        return new TypeParameter(namespaceName, typeName, name, Variance.INVARIANT);
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
