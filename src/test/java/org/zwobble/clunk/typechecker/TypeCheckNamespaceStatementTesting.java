package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.TypedNamespaceStatementNode;
import org.zwobble.clunk.ast.untyped.UntypedNamespaceStatementNode;

public class TypeCheckNamespaceStatementTesting {
    public static TypeCheckResult<TypedNamespaceStatementNode> typeCheckNamespaceStatementAllPhases(
        UntypedNamespaceStatementNode node,
        TypeCheckerContext context
    ) {
        var result = TypeChecker.typeCheckNamespaceStatement(node);

        for (var pendingTypeCheck : result.pendingTypeChecks()) {
            context = pendingTypeCheck.typeCheck(context);
        }

        return new TypeCheckResult<>(result.value(context), context);
    }
}
