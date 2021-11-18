package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.ast.typed.TypedRecordFieldNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.typed.TypedStaticExpressionNode;
import org.zwobble.clunk.ast.untyped.UntypedRecordFieldNode;
import org.zwobble.clunk.ast.untyped.UntypedRecordNode;
import org.zwobble.clunk.ast.untyped.UntypedStaticExpressionNode;
import org.zwobble.clunk.ast.untyped.UntypedStaticReferenceNode;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

import java.util.stream.Collectors;

public class TypeChecker {
    public static TypedRecordNode typeCheckRecord(UntypedRecordNode node) {
        return new TypedRecordNode(
            node.name(),
            node.fields().stream()
                .map(field -> typeCheckRecordField(field))
                .collect(Collectors.toList()),
            node.source()
        );
    }

    private static TypedRecordFieldNode typeCheckRecordField(UntypedRecordFieldNode node) {
        return new TypedRecordFieldNode(
            node.name(),
            typeCheckStaticExpressionNode(node.type()),
            node.source()
        );
    }

    private static TypedStaticExpressionNode typeCheckStaticExpressionNode(UntypedStaticExpressionNode node) {
        return node.accept(new UntypedStaticExpressionNode.Visitor<TypedStaticExpressionNode>() {
            @Override
            public TypedStaticExpressionNode visit(UntypedStaticReferenceNode node) {
                return typeCheckStaticReferenceNode(node);
            }
        });
    }

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
