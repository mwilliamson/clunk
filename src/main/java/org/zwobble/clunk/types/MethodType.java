package org.zwobble.clunk.types;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record MethodType(
    Optional<List<TypeParameter>> typeLevelParams,
    List<Type> positionalParams,
    Type returnType
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

        return "method " + typeLevelParamString + "(" + paramsString + ") -> " + returnType.describe();
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return new MethodType(
            typeLevelParams,
            positionalParams.stream()
                .map(param -> param.replace(typeMap))
                .toList(),
            returnType.replace(typeMap)
        );
    }
}
