package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.TypedInterfaceBodyDeclarationNode;
import org.zwobble.clunk.types.Type;

import java.util.Map;

public record TypeCheckInterfaceBodyDeclarationResult(
    Map<String, Type> memberTypes,
    TypedInterfaceBodyDeclarationNode value
) {
}
