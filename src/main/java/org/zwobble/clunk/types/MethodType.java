package org.zwobble.clunk.types;

import java.util.List;
import java.util.stream.Collectors;

public record MethodType(
    List<Type> positionalParams,
    Type returnType
) implements FunctionType {
    @Override
    public String describe() {
        var paramsString = positionalParams.stream()
            .map(param -> param.describe())
            .collect(Collectors.joining(", "));

        return "(" + paramsString + ") -> " + returnType.describe();
    }
}