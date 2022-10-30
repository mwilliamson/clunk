package org.zwobble.clunk.types;

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
    public String identifier() {
        return name;
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return typeMap.get(this);
    }
}
