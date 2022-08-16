package org.zwobble.clunk.typechecker;

public record TypeCheckFunctionStatementResult<T>(
    T value,
    boolean returns,
    TypeCheckerContext context
) {
    public static <T> TypeCheckFunctionStatementResult<T> neverReturns(
        T value,
        TypeCheckerContext context
    ) {
        return new TypeCheckFunctionStatementResult<>(
            value,
            false,
            context
        );
    }
}
