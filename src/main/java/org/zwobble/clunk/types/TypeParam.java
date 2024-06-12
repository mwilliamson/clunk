package org.zwobble.clunk.types;

import java.util.Optional;

public record TypeParam(
    NamespaceId namespaceId,
    Optional<String> typeName,
    Optional<String> functionName,
    String name,
    Variance variance
) implements Type {
    public static TypeParam covariant(NamespaceId namespaceId, String typeName, String name) {
        return new TypeParam(namespaceId, Optional.of(typeName), Optional.empty(), name, Variance.COVARIANT);
    }

    public static TypeParam contravariant(NamespaceId namespaceId, String typeName, String name) {
        return new TypeParam(namespaceId, Optional.of(typeName), Optional.empty(), name, Variance.CONTRAVARIANT);
    }

    public static TypeParam invariant(NamespaceId namespaceId, String typeName, String name) {
        return new TypeParam(namespaceId, Optional.of(typeName), Optional.empty(), name, Variance.INVARIANT);
    }

    public static TypeParam method(NamespaceId namespaceId, String typeName, String functionName, String name) {
        return new TypeParam(namespaceId, Optional.of(typeName), Optional.of(functionName), name, Variance.INVARIANT);
    }

    public static TypeParam method(StructuredType type, String functionName, String name) {
        return method(type.namespaceId(), type.name(), functionName, name);
    }

    public static TypeParam method(TypeConstructor typeConstructor, String functionName, String name) {
        return method(typeConstructor.genericType(), functionName, name);
    }

    public static TypeParam function(NamespaceId namespaceId, String functionName, String name) {
        return new TypeParam(namespaceId, Optional.empty(), Optional.of(functionName), name, Variance.INVARIANT);
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
