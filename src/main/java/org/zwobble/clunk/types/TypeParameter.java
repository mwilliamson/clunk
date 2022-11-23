package org.zwobble.clunk.types;

import java.util.Optional;

public record TypeParameter(
    NamespaceName namespaceName,
    String typeName,
    Optional<String> functionName,
    String name,
    Variance variance
) implements Type {
    public static TypeParameter covariant(NamespaceName namespaceName, String typeName, String name) {
        return new TypeParameter(namespaceName, typeName, Optional.empty(), name, Variance.COVARIANT);
    }

    public static TypeParameter invariant(NamespaceName namespaceName, String typeName, String name) {
        return new TypeParameter(namespaceName, typeName, Optional.empty(), name, Variance.INVARIANT);
    }

    public static TypeParameter function(NamespaceName namespaceName, String typeName, String functionName, String name) {
        return new TypeParameter(namespaceName, typeName, Optional.of(functionName), name, Variance.INVARIANT);
    }

    public static TypeParameter function(StructuredType type, String functionName, String name) {
        return new TypeParameter(type.namespaceName(), type.name(), Optional.of(functionName), name, Variance.INVARIANT);
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
