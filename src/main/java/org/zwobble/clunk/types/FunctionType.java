package org.zwobble.clunk.types;

public record FunctionType(
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
        return new FunctionType(
            params.replace(typeMap),
            returnType.replace(typeMap)
        );
    }
}
