package org.zwobble.clunk.types;

import java.util.HashMap;
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

        return "(" + paramsString + ") -> " + returnType.describe();
    }

    @Override
    public Type replace(HashMap<TypeParameter, Type> typeMap) {
        // TODO: implement properly
        return this;
    }
}
