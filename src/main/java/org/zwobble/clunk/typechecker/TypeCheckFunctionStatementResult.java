package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.types.Type;
import org.zwobble.clunk.types.Types;

import java.util.function.Function;

public record TypeCheckFunctionStatementResult<T>(
    T value,
    ReturnBehaviour returnBehaviour,
    Type returnType,
    TypeCheckerContext context
) {
    public static <T> TypeCheckFunctionStatementResult<T> neverReturns(
        T value,
        TypeCheckerContext context
    ) {
        return new TypeCheckFunctionStatementResult<>(
            value,
            ReturnBehaviour.NEVER,
            Types.NOTHING,
            context
        );
    }

    public <R> TypeCheckFunctionStatementResult<R> map(Function<T, R> func) {
        return new TypeCheckFunctionStatementResult<>(
            func.apply(value),
            returnBehaviour,
            returnType,
            context
        );
    }

    public boolean alwaysReturns() {
        return returnBehaviour.equals(ReturnBehaviour.ALWAYS);
    }
}
