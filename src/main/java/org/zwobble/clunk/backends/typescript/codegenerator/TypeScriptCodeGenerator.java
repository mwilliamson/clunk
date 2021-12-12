package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.typescript.ast.*;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

import java.util.stream.Collectors;

public class TypeScriptCodeGenerator {
    private static TypeScriptExpressionNode compileBoolLiteral(TypedBoolLiteralNode node) {
        return new TypeScriptBoolLiteralNode(node.value());
    }

    public static TypeScriptExpressionNode compileExpression(TypedExpressionNode node) {
        return node.accept(new TypedExpressionNode.Visitor<TypeScriptExpressionNode>() {
            @Override
            public TypeScriptExpressionNode visit(TypedBoolLiteralNode node) {
                return compileBoolLiteral(node);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedStringLiteralNode node) {
                return compileStringLiteral(node);
            }
        });
    }

    public static TypeScriptModuleNode compileNamespace(TypedNamespaceNode node) {
        var name = String.join("/", node.name());

        var statements = node.statements().stream()
            .map(statement -> compileNamespaceStatement(statement))
            .toList();

        return new TypeScriptModuleNode(name, statements);
    }

    private static TypeScriptInterfaceDeclarationNode compileNamespaceStatement(TypedNamespaceStatementNode node) {
        return node.accept(new TypedNamespaceStatementNode.Visitor<TypeScriptInterfaceDeclarationNode>() {
            @Override
            public TypeScriptInterfaceDeclarationNode visit(TypedFunctionNode node) {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public TypeScriptInterfaceDeclarationNode visit(TypedRecordNode node) {
                return compileRecord(node);
            }
        });
    }

    public static TypeScriptInterfaceDeclarationNode compileRecord(TypedRecordNode node) {
        var fields = node.fields().stream()
            .map(field -> new TypeScriptInterfaceFieldNode(field.name(), compileStaticExpression(field.type())))
            .collect(Collectors.toList());

        return new TypeScriptInterfaceDeclarationNode(node.name(), fields);
    }

    public static TypeScriptReferenceNode compileStaticExpression(TypedStaticExpressionNode node) {
        return new TypeScriptReferenceNode(compileType(node.type()));
    }

    private static TypeScriptExpressionNode compileStringLiteral(TypedStringLiteralNode node) {
        return new TypeScriptStringLiteralNode(node.value());
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
