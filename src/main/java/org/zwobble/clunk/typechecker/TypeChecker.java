package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.TypedStaticExpressionNode;
import org.zwobble.clunk.ast.untyped.UntypedStaticReferenceNode;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

public class TypeChecker {
    public static TypedStaticExpressionNode typeCheckStaticReferenceNode(UntypedStaticReferenceNode node) {
        var type = resolveType(node.value());
        return new TypedStaticExpressionNode(type, node.source());
    }

    private static Type resolveType(String value) {
        return switch (value) {
            case "Int" -> IntType.INSTANCE;
            case "String" -> StringType.INSTANCE;
            default -> throw new RuntimeException("TODO");
        };
    }
}
