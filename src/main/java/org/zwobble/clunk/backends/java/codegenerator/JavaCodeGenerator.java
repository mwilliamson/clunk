package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.java.ast.*;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.typechecker.SubtypeLookup;
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

    private static JavaStatementNode compileBlankLine(TypedBlankLineNode node, JavaCodeGeneratorContext context) {
        return new JavaBlankLineNode();
    }

    private static JavaExpressionNode compileBoolLiteral(TypedBoolLiteralNode node) {
        return new JavaBoolLiteralNode(node.value());
    }

    private static JavaExpressionNode compileCall(TypedCallNode node, JavaCodeGeneratorContext context) {
        var javaReceiver = compileCallReceiver(node.receiver(), context);
        var javaArgs = node.positionalArgs().stream()
            .map(arg -> compileExpression(arg, context))
            .toList();

        if (node.receiver().type() instanceof TypeLevelValueType typeLevelValueType) {
            var recordType = (RecordType) typeLevelValueType.value();
            context.addImportType(typeToJavaTypeName(recordType, context));
            return new JavaCallNewNode(javaReceiver, javaArgs);
        } else {
            return new JavaCallNode(javaReceiver, javaArgs);
        }
    }

    private static JavaExpressionNode compileCallReceiver(TypedExpressionNode receiver, JavaCodeGeneratorContext context) {
        var macro = lookupMacro(receiver.type());

        if (macro.isPresent()) {
            return macro.get().compileReceiver(context);
        } else {
            return compileExpression(receiver, context);
        }
    }

    public static JavaOrdinaryCompilationUnitNode compileEnum(TypedEnumNode node, JavaCodeGeneratorContext context) {
        var packageDeclaration = namespaceToPackage(node.type().namespaceName(), context);
        var typeDeclaration = new JavaEnumDeclarationNode(
            node.type().name(),
            node.type().members()
        );
        return new JavaOrdinaryCompilationUnitNode(packageDeclaration, List.of(), typeDeclaration);
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
            public JavaExpressionNode visit(TypedFieldAccessNode node) {
                return compileFieldAccess(node, context);
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

    private static JavaExpressionNode compileFieldAccess(TypedFieldAccessNode node, JavaCodeGeneratorContext context) {
        return new JavaCallNode(
            new JavaMemberAccessNode(
                compileExpression(node.receiver(), context),
                node.fieldName()
            ),
            List.of()
        );
    }

    public static JavaClassBodyDeclarationNode compileFunction(TypedFunctionNode node, JavaCodeGeneratorContext context) {
        return new JavaMethodDeclarationNode(
            List.of(),
            true,
            compileTypeLevelExpression(node.returnType(), context),
            node.name(),
            node.params().stream().map(param -> compileParam(param, context)).toList(),
            node.body().stream().map(statement -> compileFunctionStatement(statement, context)).toList()
        );
    }

    public static JavaStatementNode compileFunctionStatement(TypedFunctionStatementNode node, JavaCodeGeneratorContext context) {
        return node.accept(new TypedFunctionStatementNode.Visitor<JavaStatementNode>() {
            @Override
            public JavaStatementNode visit(TypedBlankLineNode node) {
                return compileBlankLine(node, context);
            }

            @Override
            public JavaStatementNode visit(TypedExpressionStatementNode node) {
                return compileExpressionStatement(node, context);
            }

            @Override
            public JavaStatementNode visit(TypedIfStatementNode node) {
                return compileIfStatement(node, context);
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

    private static JavaStatementNode compileIfStatement(
        TypedIfStatementNode node,
        JavaCodeGeneratorContext context
    ) {
        return new JavaIfStatementNode(
            node.conditionalBranches().stream()
                .map(conditionalBranch -> new JavaConditionalBranchNode(
                    compileExpression(conditionalBranch.condition(), context),
                    conditionalBranch.body().stream()
                        .map(statement -> compileFunctionStatement(statement, context))
                        .toList()
                ))
                .toList(),
            node.elseBody().stream()
                .map(statement -> compileFunctionStatement(statement, context))
                .toList()
        );
    }

    public static JavaOrdinaryCompilationUnitNode compileInterface(
        TypedInterfaceNode node,
        JavaCodeGeneratorContext context
    ) {
        var permits = context.subtypesOf(node.type()).stream()
            .map(subtype -> new JavaTypeVariableReferenceNode(subtype.name()))
            .toList();

        return new JavaOrdinaryCompilationUnitNode(
            namespaceToPackage(((InterfaceType) node.type()).namespaceName(), context),
            List.of(),
            new JavaInterfaceDeclarationNode(
                node.name(),
                Optional.of(permits)
            )
        );
    }


    private static JavaExpressionNode compileIntLiteral(TypedIntLiteralNode node) {
        return new JavaIntLiteralNode(node.value());
    }

    public static List<JavaOrdinaryCompilationUnitNode> compileNamespace(
        TypedNamespaceNode node,
        JavaTargetConfig config,
        SubtypeLookup subtypeLookup
    ) {
        var context = new JavaCodeGeneratorContext(config, subtypeLookup);
        var compilationUnits = new ArrayList<JavaOrdinaryCompilationUnitNode>();
        var functions = new ArrayList<JavaClassBodyDeclarationNode>();

        for (var statement : node.statements()) {
            statement.accept(new TypedNamespaceStatementNode.Visitor<Void>() {
                @Override
                public Void visit(TypedBlankLineNode node) {
                    return null;
                }

                @Override
                public Void visit(TypedEnumNode node) {
                    compilationUnits.add(compileEnum(node, context));
                    return null;
                }

                @Override
                public Void visit(TypedFunctionNode functionNode) {
                    functions.add(compileFunction(functionNode, context));
                    return null;
                }

                @Override
                public Void visit(TypedInterfaceNode interfaceNode) {
                    compilationUnits.add(compileInterface(interfaceNode, context));
                    return null;
                }

                @Override
                public Void visit(TypedRecordNode recordNode) {
                    compilationUnits.add(compileRecord(recordNode, context));
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

    private static JavaParamNode compileParam(TypedParamNode node, JavaCodeGeneratorContext context) {
        return new JavaParamNode(compileTypeLevelExpression(node.type(), context), node.name());
    }

    public static JavaOrdinaryCompilationUnitNode compileRecord(
        TypedRecordNode node,
        JavaCodeGeneratorContext context
    ) {
        var components = node.fields().stream()
            .map(field -> new JavaRecordComponentNode(compileTypeLevelExpression(field.type(), context), field.name()))
            .collect(Collectors.toList());

        var implements_ = context.supertypesOf(node.type()).stream()
            .map(implementsType -> new JavaTypeVariableReferenceNode(implementsType.name()))
            .toList();

        return new JavaOrdinaryCompilationUnitNode(
            namespaceToPackage(node.type().namespaceName(), context),
            List.of(),
            new JavaRecordDeclarationNode(
                node.name(),
                components,
                implements_
            )
        );
    }

    private static JavaExpressionNode compileReference(TypedReferenceNode node) {
        return new JavaReferenceNode(node.name());
    }

    private static JavaStatementNode compileReturn(TypedReturnNode node, JavaCodeGeneratorContext context) {
        return new JavaReturnNode(compileExpression(node.expression(), context));
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

    public static JavaTypeExpressionNode compileTypeLevelExpression(
        TypedTypeLevelExpressionNode node,
        JavaCodeGeneratorContext context
    ) {
        return node.accept(new TypedTypeLevelExpressionNode.Visitor<JavaTypeExpressionNode>() {
            @Override
            public JavaTypeExpressionNode visit(TypedConstructedTypeNode node) {
                // TODO: handle boxing
                return new JavaParameterizedType(
                    compileTypeLevelExpression(node.receiver(), context),
                    node.args().stream()
                        .map(arg -> compileTypeLevelExpression(arg, context))
                        .toList()
                );
            }

            @Override
            public JavaTypeExpressionNode visit(TypedTypeLevelReferenceNode node) {
                var value = node.value();

                if (value == BoolType.INSTANCE) {
                    return new JavaTypeVariableReferenceNode("boolean");
                } else if (value == IntType.INSTANCE) {
                    return new JavaTypeVariableReferenceNode("int");
                } else if (value == StringType.INSTANCE) {
                    return new JavaTypeVariableReferenceNode("String");
                } else if (value == ListTypeConstructor.INSTANCE) {
                    return new JavaFullyQualifiedTypeReferenceNode("java.util", "List");
                } else if (value == OptionTypeConstructor.INSTANCE) {
                    return new JavaFullyQualifiedTypeReferenceNode("java.util", "Optional");
                } else {
                    return new JavaTypeVariableReferenceNode(node.name());
                }
            }
        });
    }

    private static JavaStatementNode compileVar(TypedVarNode node, JavaCodeGeneratorContext context) {
        return new JavaVariableDeclarationNode(node.name(), compileExpression(node.expression(), context));
    }

    private static String namespaceToPackage(NamespaceName namespaceName, JavaCodeGeneratorContext context) {
        return context.packagePrefix() + String.join(".", namespaceName.parts());
    }

    private static String typeToJavaTypeName(RecordType recordType, JavaCodeGeneratorContext context) {
        return namespaceToPackage(recordType.namespaceName(), context) + "." + recordType.name();
    }
}
