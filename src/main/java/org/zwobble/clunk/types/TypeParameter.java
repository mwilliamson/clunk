package org.zwobble.clunk.types;

import java.util.Optional;

public record TypeParameter(
    NamespaceId namespaceId,
    Optional<String> typeName,
    Optional<String> functionName,
    String name,
    Variance variance
) implements Type {
    public static TypeParameter covariant(NamespaceId namespaceId, String typeName, String name) {
        return new TypeParameter(namespaceId, Optional.of(typeName), Optional.empty(), name, Variance.COVARIANT);
    }

    public static TypeParameter contravariant(NamespaceId namespaceId, String typeName, String name) {
        return new TypeParameter(namespaceId, Optional.of(typeName), Optional.empty(), name, Variance.CONTRAVARIANT);
    }

    public static TypeParameter invariant(NamespaceId namespaceId, String typeName, String name) {
        return new TypeParameter(namespaceId, Optional.of(typeName), Optional.empty(), name, Variance.INVARIANT);
    }

    public static TypeParameter method(NamespaceId namespaceId, String typeName, String functionName, String name) {
        return new TypeParameter(namespaceId, Optional.of(typeName), Optional.of(functionName), name, Variance.INVARIANT);
    }

    public static TypeParameter method(StructuredType type, String functionName, String name) {
        return method(type.namespaceId(), type.name(), functionName, name);
    }

    public static TypeParameter method(TypeConstructor typeConstructor, String functionName, String name) {
        return method(typeConstructor.genericType(), functionName, name);
    }

    public static TypeParameter function(NamespaceId namespaceId, String functionName, String name) {
        return new TypeParameter(namespaceId, Optional.empty(), Optional.of(functionName), name, Variance.INVARIANT);
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
