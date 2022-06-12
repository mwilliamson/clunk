package org.zwobble.clunk.typechecker;

public record TypeCheckResult<T>(
    T typedNode,
    TypeCheckerContext context
) {
}
