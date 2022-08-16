package org.zwobble.clunk.typechecker;

import java.util.function.Function;

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

    public <R> TypeCheckFunctionStatementResult<R> map(Function<T, R> func) {
        return new TypeCheckFunctionStatementResult<>(
            func.apply(value),
            returns,
            context
        );
    }
}
