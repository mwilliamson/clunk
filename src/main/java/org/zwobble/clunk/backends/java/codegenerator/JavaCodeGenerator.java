package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.java.ast.*;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.zwobble.clunk.backends.python.codegenerator.CaseConverter.lowerCamelCaseToUpperCamelCase;
import static org.zwobble.clunk.util.Lists.last;

public class JavaCodeGenerator {
    private interface JavaMacro {
        JavaExpressionNode compileReceiver(JavaCodeGeneratorContext context);
    }

    private static final Map<NamespaceName, Map<String, JavaMacro>> MACROS = Map.ofEntries(
        Map.entry(
            NamespaceName.fromParts("stdlib", "assertions"),
            Map.ofEntries(
                Map.entry("assertThat", new JavaMacro() {
                    @Override
                    public JavaExpressionNode compileReceiver(JavaCodeGeneratorContext context) {
                        context.addImportStatic("org.hamcrest.MatcherAssert", "assertThat");
                        return new JavaReferenceNode("assertThat");
                    }
                })
            )
        ),
        Map.entry(
            NamespaceName.fromParts("stdlib", "matchers"),
            Map.ofEntries(
                Map.entry("equalTo", new JavaMacro() {
                    @Override
                    public JavaExpressionNode compileReceiver(JavaCodeGeneratorContext context) {
                        context.addImportStatic("org.hamcrest.Matchers", "equalTo");
                        return new JavaReferenceNode("equalTo");
                    }
                })
            )
        )
    );

    private static Optional<JavaMacro> lookupMacro(Type type) {
        if (type instanceof StaticFunctionType staticFunctionType) {
            var macro = MACROS.getOrDefault(staticFunctionType.namespaceName(), Map.of())
                .get(staticFunctionType.functionName());
            return Optional.ofNullable(macro);
        } else {
            return Optional.empty();
        }
    }

    private static JavaExpressionNode compileBoolLiteral(TypedBoolLiteralNode node) {
        return new JavaBoolLiteralNode(node.value());
    }

    private static JavaExpressionNode compileCall(TypedCallNode node, JavaCodeGeneratorContext context) {
        return new JavaCallNode(
            compileCallReceiver(node.receiver(), context),
            node.positionalArgs().stream()
                .map(arg -> compileExpression(arg, context))
                .toList()
        );
    }

    private static JavaExpressionNode compileCallReceiver(TypedExpressionNode receiver, JavaCodeGeneratorContext context) {
        var macro = lookupMacro(receiver.type());

        if (macro.isPresent()) {
            return macro.get().compileReceiver(context);
        } else {
            return compileExpression(receiver, context);
        }
    }

    public static JavaExpressionNode compileExpression(TypedExpressionNode node, JavaCodeGeneratorContext context) {
        return node.accept(new TypedExpressionNode.Visitor<JavaExpressionNode>() {
            @Override
            public JavaExpressionNode visit(TypedBoolLiteralNode node) {
                return compileBoolLiteral(node);
            }

            @Override
            public JavaExpressionNode visit(TypedCallNode node) {
                return compileCall(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedIntLiteralNode node) {
                return compileIntLiteral(node);
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

    private static JavaStatementNode compileExpressionStatement(TypedExpressionStatementNode node, JavaCodeGeneratorContext context) {
        return new JavaExpressionStatementNode(compileExpression(node.expression(), context));
    }

    public static JavaClassBodyDeclarationNode compileFunction(TypedFunctionNode node, JavaCodeGeneratorContext context) {
        return new JavaMethodDeclarationNode(
            List.of(),
            true,
            compileStaticExpression(node.returnType()),
            node.name(),
            node.params().stream().map(param -> compileParam(param)).toList(),
            node.body().stream().map(statement -> compileFunctionStatement(statement, context)).toList()
        );
    }

    public static JavaStatementNode compileFunctionStatement(TypedFunctionStatementNode node, JavaCodeGeneratorContext context) {
        return node.accept(new TypedFunctionStatementNode.Visitor<JavaStatementNode>() {
            @Override
            public JavaStatementNode visit(TypedExpressionStatementNode node) {
                return compileExpressionStatement(node, context);
            }

            @Override
            public JavaStatementNode visit(TypedFunctionStatementNode node) {
                throw new UnsupportedOperationException("TODO");
            }

            @Override
            public JavaStatementNode visit(TypedReturnNode node) {
                return compileReturn(node, context);
            }

            @Override
            public JavaStatementNode visit(TypedVarNode node) {
                return compileVar(node, context);
            }
        });
    }

    private static JavaExpressionNode compileIntLiteral(TypedIntLiteralNode node) {
        return new JavaIntLiteralNode(node.value());
    }

    public static List<JavaOrdinaryCompilationUnitNode> compileNamespace(TypedNamespaceNode node, JavaTargetConfig config) {
        var context = new JavaCodeGeneratorContext(config);
        var compilationUnits = new ArrayList<JavaOrdinaryCompilationUnitNode>();
        var functions = new ArrayList<JavaClassBodyDeclarationNode>();

        for (var statement : node.statements()) {
            statement.accept(new TypedNamespaceStatementNode.Visitor<Void>() {
                @Override
                public Void visit(TypedFunctionNode functionNode) {
                    functions.add(compileFunction(functionNode, context));
                    return null;
                }

                @Override
                public Void visit(TypedRecordNode recordNode) {
                    compilationUnits.add(compileRecord(node.name(), recordNode, context));
                    return null;
                }

                @Override
                public Void visit(TypedTestNode testNode) {
                    functions.add(compileTest(testNode, context));
                    return null;
                }
            });
        }

        if (!functions.isEmpty()) {
            compilationUnits.add(new JavaOrdinaryCompilationUnitNode(
                namespaceToPackage(node.name(), context),
                context.imports().stream().toList(),
                new JavaClassDeclarationNode(lowerCamelCaseToUpperCamelCase(last(node.name().parts())), functions)
            ));
        }

        return compilationUnits;
    }

    private static JavaParamNode compileParam(TypedParamNode node) {
        return new JavaParamNode(compileStaticExpression(node.type()), node.name());
    }

    public static JavaOrdinaryCompilationUnitNode compileRecord(
        NamespaceName namespace,
        TypedRecordNode node,
        JavaCodeGeneratorContext context
    ) {
        var components = node.fields().stream()
            .map(field -> new JavaRecordComponentNode(compileStaticExpression(field.type()), field.name()))
            .collect(Collectors.toList());

        return new JavaOrdinaryCompilationUnitNode(
            namespaceToPackage(namespace, context),
            List.of(),
            new JavaRecordDeclarationNode(
                node.name(),
                components
            )
        );
    }

    private static JavaExpressionNode compileReference(TypedReferenceNode node) {
        return new JavaReferenceNode(node.name());
    }

    private static JavaStatementNode compileReturn(TypedReturnNode node, JavaCodeGeneratorContext context) {
        return new JavaReturnNode(compileExpression(node.expression(), context));
    }

    public static JavaTypeVariableReferenceNode compileStaticExpression(TypedStaticExpressionNode node) {
        return new JavaTypeVariableReferenceNode(compileType(node.type()));
    }

    private static JavaStringLiteralNode compileStringLiteral(TypedStringLiteralNode node) {
        return new JavaStringLiteralNode(node.value());
    }

    public static JavaClassBodyDeclarationNode compileTest(TypedTestNode node, JavaCodeGeneratorContext context) {
        var method = JavaMethodDeclarationNode.builder()
            .addAnnotation(Java.annotation(Java.fullyQualifiedTypeReference("org.junit.jupiter.api", "Test")))
            .addAnnotation(Java.annotation(
                Java.fullyQualifiedTypeReference("org.junit.jupiter.api", "DisplayName"),
                Java.string(node.name())
            ))
            .isStatic(false)
            .name(JavaTestNames.generateName(node.name()));

        for (var statement : node.body()) {
            method = method.addBodyStatement(compileFunctionStatement(statement, context));
        }

        return method.build();
    }

    private static JavaStatementNode compileVar(TypedVarNode node, JavaCodeGeneratorContext context) {
        return new JavaVariableDeclarationNode(node.name(), compileExpression(node.expression(), context));
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

    private static String namespaceToPackage(NamespaceName namespace, JavaCodeGeneratorContext context) {
        return context.packagePrefix() + String.join(".", namespace.parts());
    }
}
