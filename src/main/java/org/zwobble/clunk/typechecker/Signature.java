package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.Type;

import java.util.List;

public record Signature(
    List<Type> positionalParams,
    Type returnType
) {
}
