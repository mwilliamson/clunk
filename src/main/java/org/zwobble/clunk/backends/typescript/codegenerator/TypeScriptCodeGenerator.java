package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.typescript.ast.*;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

import java.util.List;
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

    private static TypeScriptStatementNode compileFunction(TypedFunctionNode node) {
        return new TypeScriptFunctionDeclarationNode(
            node.name(),
            node.params().stream().map(param -> compileParam(param)).toList(),
            compileStaticExpression(node.returnType()),
            List.of()
        );
    }

    public static TypeScriptStatementNode compileFunctionStatement(TypedFunctionStatementNode node) {
        return node.accept(new TypedFunctionStatementNode.Visitor<TypeScriptStatementNode>() {
            @Override
            public TypeScriptStatementNode visit(TypedReturnNode node) {
                return compileReturn(node);
            }
        });
    }

    private static TypeScriptParamNode compileParam(TypedParamNode node) {
        return new TypeScriptParamNode(node.name(), compileStaticExpression(node.type()));
    }

    public static TypeScriptModuleNode compileNamespace(TypedNamespaceNode node) {
        var name = String.join("/", node.name());

        var statements = node.statements().stream()
            .map(statement -> compileNamespaceStatement(statement))
            .toList();

        return new TypeScriptModuleNode(name, statements);
    }

    public static TypeScriptStatementNode compileNamespaceStatement(TypedNamespaceStatementNode node) {
        return node.accept(new TypedNamespaceStatementNode.Visitor<TypeScriptStatementNode>() {
            @Override
            public TypeScriptStatementNode visit(TypedFunctionNode node) {
                return compileFunction(node);
            }

            @Override
            public TypeScriptStatementNode visit(TypedRecordNode node) {
                return compileRecord(node);
            }
        });
    }

    private static TypeScriptInterfaceDeclarationNode compileRecord(TypedRecordNode node) {
        var fields = node.fields().stream()
            .map(field -> new TypeScriptInterfaceFieldNode(field.name(), compileStaticExpression(field.type())))
            .collect(Collectors.toList());

        return new TypeScriptInterfaceDeclarationNode(node.name(), fields);
    }

    private static TypeScriptStatementNode compileReturn(TypedReturnNode node) {
        return new TypeScriptReturnNode(compileExpression(node.expression()));
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
