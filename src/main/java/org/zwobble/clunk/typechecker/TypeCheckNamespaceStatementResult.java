package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.TypedNamespaceStatementNode;
import org.zwobble.clunk.types.Type;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public record TypeCheckNamespaceStatementResult(
    List<PendingTypeCheck> pendingTypeChecks,
    Function<TypeCheckerContext, TypedNamespaceStatementNode> value,
    Supplier<Optional<Map.Entry<String, Type>>> fieldTypeSupplier
) {
    public TypedNamespaceStatementNode value(TypeCheckerContext context) {
        return value.apply(context);
    }

    public Optional<Map.Entry<String, Type>> fieldType() {
        return fieldTypeSupplier.get();
    }
}
