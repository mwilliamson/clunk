package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.ast.typed.TypedNamespaceNode;
import org.zwobble.clunk.ast.typed.TypedNamespaceStatementNode;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.ast.typed.TypedStaticExpressionNode;
import org.zwobble.clunk.backends.python.ast.*;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

import java.util.ArrayList;
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

    public static PythonModuleNode compileNamespace(TypedNamespaceNode node) {
        var moduleName = String.join(".", node.name());

        var statements = new ArrayList<PythonStatementNode>();
        statements.add(new PythonImportNode("dataclasses"));

        node.statements().stream()
            .map(statement -> compileStatement(statement))
            .forEachOrdered(statements::add);

        return new PythonModuleNode(moduleName, statements);
    }

    public static PythonClassDeclarationNode compileRecord(TypedRecordNode node) {
        var decorators = List.of(
            Python.call(
                Python.attr(Python.reference("dataclasses"), "dataclass"),
                List.of(Python.kwarg("frozen", Python.TRUE))
            )
        );

        var statements = node.fields().stream()
            .map(field -> Python.variableType(field.name(), compileStaticExpression(field.type())))
            .toList();

        return new PythonClassDeclarationNode(node.name(), decorators, statements);
    }

    public static PythonStatementNode compileStatement(TypedNamespaceStatementNode node) {
        return node.accept(new TypedNamespaceStatementNode.Visitor<PythonStatementNode>() {
            @Override
            public PythonStatementNode visit(TypedRecordNode node) {
                return compileRecord(node);
            }
        });
    }
}
