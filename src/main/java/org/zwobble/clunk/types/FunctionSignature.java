package org.zwobble.clunk.types;

public record FunctionSignature(
    ParamTypes params,
    Type returnType
) {
}
