package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.TypedNamespaceStatementNode;
import org.zwobble.clunk.ast.untyped.UntypedNamespaceStatementNode;
import org.zwobble.clunk.types.Type;

import java.util.Map;
import java.util.Optional;

public class TypeCheckNamespaceStatementTesting {
    public record TypeCheckNamespaceStatementAllPhasesResult(
        TypeCheckNamespaceStatementResult result,
        TypeCheckerContext context
    ) {
        public TypedNamespaceStatementNode typedNode() {
            return result.value(context);
        }

        public Optional<Map.Entry<String, Type>> fieldType() {
            return result.fieldType();
        }
    }

    public static TypeCheckNamespaceStatementAllPhasesResult typeCheckNamespaceStatementAllPhases(
        UntypedNamespaceStatementNode node,
        TypeCheckerContext context
    ) {
        var result = TypeChecker.typeCheckNamespaceStatement(node);

        for (var pendingTypeCheck : result.pendingTypeChecks()) {
            context = pendingTypeCheck.typeCheck(context);
        }

        return new TypeCheckNamespaceStatementAllPhasesResult(result, context);
    }
}
