package org.zwobble.clunk.types;

import java.util.Optional;

public record TypeParameter(
    NamespaceId namespaceId,
    String typeName,
    Optional<String> functionName,
    String name,
    Variance variance
) implements Type {
    public static TypeParameter covariant(NamespaceId namespaceId, String typeName, String name) {
        return new TypeParameter(namespaceId, typeName, Optional.empty(), name, Variance.COVARIANT);
    }

    public static TypeParameter invariant(NamespaceId namespaceId, String typeName, String name) {
        return new TypeParameter(namespaceId, typeName, Optional.empty(), name, Variance.INVARIANT);
    }

    public static TypeParameter function(NamespaceId namespaceId, String typeName, String functionName, String name) {
        return new TypeParameter(namespaceId, typeName, Optional.of(functionName), name, Variance.INVARIANT);
    }

    public static TypeParameter function(StructuredType type, String functionName, String name) {
        return new TypeParameter(type.namespaceId(), type.name(), Optional.of(functionName), name, Variance.INVARIANT);
    }

    public static TypeParameter function(TypeConstructor typeConstructor, String functionName, String name) {
        return function(typeConstructor.genericType(), functionName, name);
    }

    @Override
    public String describe() {
        return name;
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return typeMap.get(this);
    }
}
