package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.typed.TypedStaticExpressionNode;
import org.zwobble.clunk.backends.python.ast.PythonAssignmentNode;
import org.zwobble.clunk.backends.python.ast.PythonAttrAccessNode;
import org.zwobble.clunk.backends.python.ast.PythonClassDeclarationNode;
import org.zwobble.clunk.backends.python.ast.PythonReferenceNode;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

import java.util.List;

public class PythonCodeGenerator {
    public static PythonReferenceNode compileStaticExpression(TypedStaticExpressionNode node) {
        return new PythonReferenceNode(compileType(node.type()));
    }

    private static String compileType(Type type) {
        if (type == BoolType.INSTANCE) {
            return "bool";
        } else if (type == IntType.INSTANCE) {
            return "int";
        } else if (type == StringType.INSTANCE) {
            return "str";
        } else {
            throw new RuntimeException("TODO");
        }
    }

    public static PythonClassDeclarationNode compileRecord(TypedRecordNode node) {
        var decorators = List.of(
            new PythonAttrAccessNode(new PythonReferenceNode("dataclasses"), "dataclass")
        );

        var statements = node.fields().stream()
            .map(field -> new PythonAssignmentNode(field.name(), compileStaticExpression(field.type())))
            .toList();

        return new PythonClassDeclarationNode(node.name(), decorators, statements);
    }
}
