package org.zwobble.clunk.typechecker;

import java.util.function.Function;

public record PendingTypeCheck(
    TypeCheckerPhase phase,
    Function<TypeCheckerContext, TypeCheckerContext> typeCheck
) {
    public TypeCheckerContext typeCheck(TypeCheckerContext context) {
        return typeCheck.apply(context);
    }
}
