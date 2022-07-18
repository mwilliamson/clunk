package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.TypedNamespaceStatementNode;

import java.util.List;
import java.util.function.Function;

public record TypeCheckNamespaceStatementResult(
    List<PendingTypeCheck> pendingTypeChecks,
    Function<TypeCheckerContext, TypedNamespaceStatementNode> value
) {
    public TypedNamespaceStatementNode value(TypeCheckerContext context) {
        return value.apply(context);
    }
}
