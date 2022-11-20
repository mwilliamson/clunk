package org.zwobble.clunk.types;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record ConstructorType(
    NamespaceName namespaceName,
    Optional<List<TypeParameter>> typeLevelParams,
    List<Type> positionalParams,
    StructuredType returnType,
    Visibility visibility
) implements CallableType {
    @Override
    public String describe() {
        var typeLevelParamString = typeLevelParams.isEmpty()
            ? ""
            : "[" + typeLevelParams.get().stream()
                .map(param -> param.describe())
                .collect(Collectors.joining(", ")) + "]";

        var paramsString = positionalParams.stream()
            .map(param -> param.describe())
            .collect(Collectors.joining(", "));

        return "constructor " + typeLevelParamString + "(" + paramsString + ") -> " + returnType.describe();
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return new ConstructorType(
            namespaceName,
            typeLevelParams,
            positionalParams.stream()
                .map(param -> param.replace(typeMap))
                .toList(),
            returnType.replace(typeMap),
            visibility
        );
    }

    @Override
    public CallableType withoutTypeParams() {
        if (typeLevelParams.isEmpty()) {
            return this;
        } else {
            return new ConstructorType(namespaceName, Optional.empty(), positionalParams, returnType, visibility);
        }
    }
}
