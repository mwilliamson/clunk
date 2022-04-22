package org.zwobble.clunk.types;

import java.util.List;
import java.util.stream.Collectors;

public record StaticFunctionType(
    List<String> namespaceName,
    String functionName,
    List<Type> positionalParams,
    Type returnType
) implements Type {
    @Override
    public String describe() {
        var paramsString = positionalParams.stream()
            .map(param -> param.describe())
            .collect(Collectors.joining(", "));

        return String.join(".", namespaceName) + "." + functionName + ": (" + paramsString + ") -> " + returnType.describe();
    }
}
