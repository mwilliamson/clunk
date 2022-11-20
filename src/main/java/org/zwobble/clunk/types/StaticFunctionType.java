package org.zwobble.clunk.types;

import java.util.List;
import java.util.stream.Collectors;

public record StaticFunctionType(
    NamespaceName namespaceName,
    String functionName,
    List<Type> positionalParams,
    Type returnType
) implements CallableType {
    @Override
    public String describe() {
        var paramsString = positionalParams.stream()
            .map(param -> param.describe())
            .collect(Collectors.joining(", "));

        return namespaceName + "." + functionName + ": (" + paramsString + ") -> " + returnType.describe();
    }

    @Override
    public Type replace(TypeMap typeMap) {
        // TODO: implement properly
        return this;
    }
}
