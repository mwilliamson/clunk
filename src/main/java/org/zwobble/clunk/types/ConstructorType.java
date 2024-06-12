package org.zwobble.clunk.types;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record ConstructorType(
    NamespaceId namespaceId,
    Optional<List<TypeParam>> typeLevelParams,
    ParamTypes params,
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

        var paramsString = params.describe();

        return "constructor " + typeLevelParamString + "(" + paramsString + ") -> " + returnType.describe();
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return new ConstructorType(
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
            return new ConstructorType(
                namespaceId,
                Optional.empty(),
                params,
                returnType,
                visibility
            );
        }
    }

    public ConstructorType withTypeParams(List<TypeParam> typeParams) {
        return new ConstructorType(
            namespaceId,
            Optional.of(typeParams),
            params,
            returnType,
            visibility
        );
    }

    public ConstructorType withReturnType(StructuredType returnType) {
        return new ConstructorType(
            namespaceId,
            typeLevelParams,
            params,
            returnType,
            visibility
        );
    }
}
