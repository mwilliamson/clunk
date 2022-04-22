package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.TypedFunctionStatementNode;

public record TypeCheckFunctionStatementResult(
    TypedFunctionStatementNode typedNode,
    TypeCheckerContext context
) {
}
