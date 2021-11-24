package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.typed.TypedStaticExpressionNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptInterfaceDeclarationNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptInterfaceFieldNode;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptReferenceNode;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

import java.util.stream.Collectors;

public class TypeScriptCodeGenerator {
    public static TypeScriptInterfaceDeclarationNode compileRecord(TypedRecordNode node) {
        var fields = node.fields().stream()
            .map(field -> new TypeScriptInterfaceFieldNode(field.name(), compileStaticExpression(field.type())))
            .collect(Collectors.toList());

        return new TypeScriptInterfaceDeclarationNode(node.name(), fields);
    }

    private static TypeScriptReferenceNode compileStaticExpression(TypedStaticExpressionNode node) {
        return new TypeScriptReferenceNode(compileType(node.type()));
    }

    private static String compileType(Type type) {
        if (type == BoolType.INSTANCE) {
            return "boolean";
        } else if (type == IntType.INSTANCE) {
            return "number";
        } else if (type == StringType.INSTANCE) {
            return "string";
        } else {
            throw new RuntimeException("TODO");
        }
    }
}
