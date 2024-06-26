package org.zwobble.clunk.types;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record MethodType(
    NamespaceId namespaceId,
    Optional<List<TypeParam>> typeLevelParams,
    ParamTypes params,
    Type returnType,
    Visibility visibility
) implements CallableType {
    @Override
    public String describe() {
        var typeLevelParamString = typeLevelParams.isEmpty()
            ? ""
            : "[" + typeLevelParams.get().stream()
                .map(param -> param.describe())
                .collect(Collectors.joining(", ")) + "]";

        var paramsString = params.describe();

        return "method " + typeLevelParamString + "(" + paramsString + ") -> " + returnType.describe();
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return new MethodType(
            namespaceId,
            typeLevelParams,
            params.replace(typeMap),
            returnType.replace(typeMap),
            visibility
        );
    }

    @Override
    public CallableType withoutTypeParams() {
        if (typeLevelParams.isEmpty()) {
            return this;
        } else {
            return new MethodType(
                namespaceId,
                Optional.empty(),
                params,
                returnType,
                visibility
            );
        }
    }
}
