package org.zwobble.clunk.typechecker;

public record TypeCheckFunctionStatementResult<T>(
    T value,
    boolean returns,
    TypeCheckerContext context
) {
}
