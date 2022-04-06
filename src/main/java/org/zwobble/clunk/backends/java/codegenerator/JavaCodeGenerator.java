package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.java.ast.*;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;
import org.zwobble.clunk.types.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.zwobble.clunk.backends.python.codegenerator.CaseConverter.lowerCamelCaseToUpperCamelCase;
import static org.zwobble.clunk.util.Lists.last;

public class JavaCodeGenerator {
    private static JavaExpressionNode compileBoolLiteral(TypedBoolLiteralNode node) {
        return new JavaBoolLiteralNode(node.value());
    }

    public static JavaExpressionNode compileExpression(TypedExpressionNode node) {
        return node.accept(new TypedExpressionNode.Visitor<JavaExpressionNode>() {
            @Override
            public JavaExpressionNode visit(TypedBoolLiteralNode node) {
                return compileBoolLiteral(node);
            }

            @Override
            public JavaExpressionNode visit(TypedReferenceNode node) {
                return compileReference(node);
            }

            @Override
            public JavaExpressionNode visit(TypedStringLiteralNode node) {
                return compileStringLiteral(node);
            }
        });
    }

    public static JavaClassBodyDeclarationNode compileFunction(TypedFunctionNode node) {
        return new JavaMethodDeclarationNode(
            true,
            compileStaticExpression(node.returnType()),
            node.name(),
            node.params().stream().map(param -> compileParam(param)).toList(),
            node.body().stream().map(statement -> compileFunctionStatement(statement)).toList()
        );
    }

    public static JavaStatementNode compileFunctionStatement(TypedFunctionStatementNode node) {
        return node.accept(new TypedFunctionStatementNode.Visitor<JavaStatementNode>() {
            @Override
            public JavaStatementNode visit(TypedReturnNode node) {
                return compileReturn(node);
            }

            @Override
            public JavaStatementNode visit(TypedVarNode node) {
                return compileVar(node);
            }
        });
    }

    public static List<JavaOrdinaryCompilationUnitNode> compileNamespace(TypedNamespaceNode node) {
        var compilationUnits = new ArrayList<JavaOrdinaryCompilationUnitNode>();
        var functions = new ArrayList<JavaClassBodyDeclarationNode>();

        for (var statement : node.statements()) {
            statement.accept(new TypedNamespaceStatementNode.Visitor<Void>() {
                @Override
                public Void visit(TypedFunctionNode functionNode) {
                    functions.add(compileFunction(functionNode));
                    return null;
                }

                @Override
                public Void visit(TypedRecordNode recordNode) {
                    compilationUnits.add(compileRecord(node.name(), recordNode));
                    return null;
                }

                @Override
                public Void visit(TypedTestNode node) {
                    throw new UnsupportedOperationException("TODO");
                }
            });
        }

        if (!functions.isEmpty()) {
            compilationUnits.add(new JavaOrdinaryCompilationUnitNode(
                namespaceToPackage(node.name()),
                new JavaClassDeclarationNode(lowerCamelCaseToUpperCamelCase(last(node.name())), functions)
            ));
        }

        return compilationUnits;
    }

    private static JavaParamNode compileParam(TypedParamNode node) {
        return new JavaParamNode(compileStaticExpression(node.type()), node.name());
    }

    public static JavaOrdinaryCompilationUnitNode compileRecord(List<String> namespace, TypedRecordNode node) {
        var components = node.fields().stream()
            .map(field -> new JavaRecordComponentNode(compileStaticExpression(field.type()), field.name()))
            .collect(Collectors.toList());

        return new JavaOrdinaryCompilationUnitNode(
            namespaceToPackage(namespace),
            new JavaRecordDeclarationNode(
                node.name(),
                components
            )
        );
    }

    private static JavaExpressionNode compileReference(TypedReferenceNode node) {
        return new JavaReferenceNode(node.name());
    }

    private static JavaStatementNode compileReturn(TypedReturnNode node) {
        return new JavaReturnNode(compileExpression(node.expression()));
    }

    public static JavaTypeReferenceNode compileStaticExpression(TypedStaticExpressionNode node) {
        return new JavaTypeReferenceNode(compileType(node.type()));
    }

    private static JavaStringLiteralNode compileStringLiteral(TypedStringLiteralNode node) {
        return new JavaStringLiteralNode(node.value());
    }

    private static JavaStatementNode compileVar(TypedVarNode node) {
        return new JavaVariableDeclarationNode(node.name(), compileExpression(node.expression()));
    }

    private static String compileType(Type type) {
        if (type == BoolType.INSTANCE) {
            return "boolean";
        } else if (type == IntType.INSTANCE) {
            return "int";
        } else if (type == StringType.INSTANCE) {
            return "String";
        } else {
            throw new RuntimeException("TODO");
        }
    }

    private static String namespaceToPackage(List<String> namespace) {
        return String.join(".", namespace);
    }
}
