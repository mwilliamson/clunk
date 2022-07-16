package org.zwobble.clunk.typechecker;

public record TypeCheckStatementResult<T>(
    T value,
    boolean returns,
    TypeCheckerContext context
) {
}
