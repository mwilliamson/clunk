package org.zwobble.clunk.types;

import java.util.List;
import java.util.Optional;

public record StaticFunctionType(
    NamespaceName namespaceName,
    String functionName,
    ParamTypes params,
    Type returnType,
    Visibility visibility
) implements CallableType {
    @Override
    public Optional<List<TypeParameter>> typeLevelParams() {
        return Optional.empty();
    }

    @Override
    public CallableType withoutTypeParams() {
        return this;
    }

    @Override
    public String describe() {
        var paramsString = params.describe();

        return namespaceName + "." + functionName + ": (" + paramsString + ") -> " + returnType.describe();
    }

    @Override
    public Type replace(TypeMap typeMap) {
        // TODO: implement properly
        return this;
    }
}
