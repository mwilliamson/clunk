package org.zwobble.clunk.types;

public record FunctionValueType(
    ParamTypes params,
    Type returnType
) implements Type {
    @Override
    public String describe() {
        var paramsString = params.describe();

        return "fn (" + paramsString + ") -> " + returnType.describe();
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return new FunctionValueType(
            params.replace(typeMap),
            returnType.replace(typeMap)
        );
    }
}
