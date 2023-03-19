package org.zwobble.clunk.types;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record StaticFunctionType(
    NamespaceId namespaceId,
    String functionName,
    Optional<List<TypeParameter>> typeLevelParams,
    ParamTypes params,
    Type returnType,
    Visibility visibility
) implements CallableType {
    @Override
    public CallableType withoutTypeParams() {
        return new StaticFunctionType(
            namespaceId,
            functionName,
            Optional.empty(),
            params,
            returnType,
            visibility
        );
    }

    @Override
    public String describe() {
        var typeLevelParamString = typeLevelParams.isEmpty()
            ? ""
            : "[" + typeLevelParams.get().stream()
            .map(param -> param.describe())
            .collect(Collectors.joining(", ")) + "]";

        var paramsString = params.describe();

        return namespaceId + "." + functionName + ": " + typeLevelParamString + "(" + paramsString + ") -> " + returnType.describe();
    }

    @Override
    public Type replace(TypeMap typeMap) {
        // TODO: implement properly
        return this;
    }
}
