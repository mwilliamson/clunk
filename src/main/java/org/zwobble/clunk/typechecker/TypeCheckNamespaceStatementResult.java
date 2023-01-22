package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.TypedNamespaceStatementNode;
import org.zwobble.clunk.types.Type;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public record TypeCheckNamespaceStatementResult(
    List<PendingTypeCheck> pendingTypeChecks,
    Supplier<TypedNamespaceStatementNode> valueSupplier,
    Supplier<Optional<Map.Entry<String, Type>>> fieldTypeSupplier
) {
    public TypeCheckerContext typeCheckPhase(TypeCheckerPhase phase, TypeCheckerContext context) {
        for (var typeCheck : pendingTypeChecks) {
            if (typeCheck.phase().equals(phase)) {
                context = typeCheck.typeCheck(context);
            }
        }
        return context;
    }

    public TypedNamespaceStatementNode value() {
        return valueSupplier.get();
    }

    public Optional<Map.Entry<String, Type>> fieldType() {
        return fieldTypeSupplier.get();
    }
}
