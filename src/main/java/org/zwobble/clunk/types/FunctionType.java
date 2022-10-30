package org.zwobble.clunk.types;

import java.util.List;
import java.util.stream.Collectors;

public record FunctionType(
    List<Type> positionalParams,
    Type returnType
) implements Type {
    @Override
    public String describe() {
        var paramsString = positionalParams.stream()
            .map(param -> param.describe())
            .collect(Collectors.joining(", "));

        return "fn (" + paramsString + ") -> " + returnType.describe();
    }

    @Override
    public String identifier() {
        return "Function";
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return new FunctionType(
            positionalParams.stream()
                .map(param -> param.replace(typeMap))
                .toList(),
            returnType.replace(typeMap)
        );
    }
}
