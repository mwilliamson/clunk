package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.Type;

import java.util.List;

public record Signature(
    SignatureType signatureType,
    List<Type> positionalParams,
    Type returnType
) {
}
