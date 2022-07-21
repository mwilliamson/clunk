package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.TypedNamespaceStatementNode;
import org.zwobble.clunk.ast.typed.TypedRecordBodyDeclarationNode;
import org.zwobble.clunk.types.Type;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public record TypeCheckRecordBodyDeclarationResult(
    Map<String, Type> memberTypes,
    Function<TypeCheckerContext, TypedRecordBodyDeclarationNode> value
) {
    public TypedRecordBodyDeclarationNode value(TypeCheckerContext context) {
        return value.apply(context);
    }
}
