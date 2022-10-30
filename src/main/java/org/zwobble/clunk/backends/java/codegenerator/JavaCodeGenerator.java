package org.zwobble.clunk.backends.java.codegenerator;

import org.zwobble.clunk.ast.SourceType;
import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.java.ast.*;
import org.zwobble.clunk.backends.java.config.JavaTargetConfig;
import org.zwobble.clunk.types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.zwobble.clunk.backends.CaseConverter.lowerCamelCaseToUpperCamelCase;
import static org.zwobble.clunk.backends.CaseConverter.upperCamelCaseToLowerCamelCase;
import static org.zwobble.clunk.util.Lists.last;

public class JavaCodeGenerator {
    private static List<JavaExpressionNode> compileArgs(List<TypedExpressionNode> positionalArgs, JavaCodeGeneratorContext context) {
        return positionalArgs.stream()
            .map(arg -> compileExpression(arg, context))
            .toList();
    }

    private static JavaBlankLineNode compileBlankLine(TypedBlankLineNode node, JavaCodeGeneratorContext context) {
        return new JavaBlankLineNode();
    }

    private static JavaExpressionNode compileBoolLiteral(TypedBoolLiteralNode node) {
        return new JavaBoolLiteralNode(node.value());
    }

    private static JavaExpressionNode compileCallConstructor(TypedCallConstructorNode node, JavaCodeGeneratorContext context) {
        var javaReceiver = compileExpression(node.receiver(), context);
        var javaArgs = compileArgs(node.positionalArgs(), context);

        var classMacro = JavaMacros.lookupClassMacro(node.type());
        if (classMacro.isPresent()) {
            return classMacro.get().compileConstructorCall(javaArgs);
        } else {
            // TODO: handle not a record type
            var recordType = (RecordType) node.type();
            context.addImportType(typeToJavaTypeName(recordType, context));
            return new JavaCallNewNode(javaReceiver, Optional.empty(), javaArgs, Optional.empty());
        }
    }

    private static JavaExpressionNode compileCallMethod(TypedCallMethodNode node, JavaCodeGeneratorContext context) {
        var classMacro = JavaMacros.lookupClassMacro(node.receiver().type());
        if (classMacro.isPresent()) {
            return classMacro.get().compileMethodCall(
                compileExpression(node.receiver(), context),
                node.methodName(),
                compileArgs(node.positionalArgs(), context)
            );
        }

        return new JavaCallNode(
            new JavaMemberAccessNode(
                compileExpression(node.receiver(), context),
                node.methodName()
            ),
            compileArgs(node.positionalArgs(), context)
        );
    }

    private static JavaExpressionNode compileCallStaticFunction(TypedCallStaticFunctionNode node, JavaCodeGeneratorContext context) {
        var macro = JavaMacros.lookupStaticFunctionMacro(node.receiverType());

        if (macro.isPresent()) {
            return new JavaCallNode(
                macro.get().compileReceiver(context),
                compileArgs(node.positionalArgs(), context)
            );
        } else {
            return new JavaCallNode(
                compileExpression(node.receiver(), context),
                compileArgs(node.positionalArgs(), context)
            );
        }
    }

    private static JavaExpressionNode compileCastUnsafe(
        TypedCastUnsafeNode node,
        JavaCodeGeneratorContext context
    ) {
        return new JavaCastNode(
            compileTypeLevelExpression(node.typeExpression(), context),
            compileExpression(node.expression(), context)
        );
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
            public JavaExpressionNode visit(TypedCallConstructorNode node) {
                return compileCallConstructor(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedCallMethodNode node) {
                return compileCallMethod(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedCallStaticFunctionNode node) {
                return compileCallStaticFunction(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedCastUnsafeNode node) {
                return compileCastUnsafe(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedIndexNode node) {
                return compileIndex(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedInstanceOfNode node) {
                return compileInstanceOf(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedIntAddNode node) {
                return compileIntAdd(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedIntEqualsNode node) {
                return compileIntEquals(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedIntLiteralNode node) {
                return compileIntLiteral(node);
            }

            @Override
            public JavaExpressionNode visit(TypedListLiteralNode node) {
                return compileListLiteral(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedLocalReferenceNode node) {
                return compileLocalReference(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedLogicalAndNode node) {
                return compileLogicalAnd(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedLogicalNotNode node) {
                return compileLogicalNot(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedLogicalOrNode node) {
                return compileLogicalOr(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedMemberAccessNode node) {
                return compileMemberAccess(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedMemberReferenceNode node) {
                return compileMemberReference(node);
            }

            @Override
            public JavaExpressionNode visit(TypedStaticMethodToFunctionNode node) {
                return compileStaticMethodToFunction(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedStringEqualsNode node) {
                return compileStringEquals(node, context);
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

    private static JavaStatementNode compileForEach(
        TypedForEachNode node,
        JavaCodeGeneratorContext context
    ) {
        return new JavaForEachNode(
            node.targetName(),
            compileExpression(node.iterable(), context),
            node.body().stream()
                .map(statement -> compileFunctionStatement(statement, context))
                .toList()
        );
    }

    public static JavaClassBodyDeclarationNode compileFunction(TypedFunctionNode node, JavaCodeGeneratorContext context) {
        return new JavaMethodDeclarationNode(
            List.of(),
            true,
            List.of(),
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
            public JavaStatementNode visit(TypedForEachNode node) {
                return compileForEach(node, context);
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
            public JavaStatementNode visit(TypedSingleLineCommentNode node) {
                return compileSingleLineComment(node);
            }

            @Override
            public JavaStatementNode visit(TypedSwitchNode node) {
                return compileSwitch(node, context);
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

    private static JavaExpressionNode compileIndex(
        TypedIndexNode node,
        JavaCodeGeneratorContext context
    ) {
        return new JavaCallNode(
            new JavaMemberAccessNode(
                compileExpression(node.receiver(), context),
                "get"
            ),
            List.of(compileExpression(node.index(), context))
        );
    }

    private static JavaExpressionNode compileInstanceOf(
        TypedInstanceOfNode node,
        JavaCodeGeneratorContext context
    ) {
        return new JavaInstanceOfNode(
            compileExpression(node.expression(), context),
            compileTypeLevelExpression(node.typeExpression(), context)
        );
    }

    public static JavaOrdinaryCompilationUnitNode compileInterface(
        TypedInterfaceNode node,
        JavaCodeGeneratorContext context
    ) {
        var permits = context.sealedInterfaceCases(node.type()).stream()
            .map(subtype -> new JavaTypeVariableReferenceNode(subtype.identifier()))
            .toList();

        var visitorMethods = context.sealedInterfaceCases(node.type()).stream()
            .map(subtype -> new JavaInterfaceMethodDeclarationNode(
                List.of(),
                new JavaTypeVariableReferenceNode("T"),
                "visit",
                List.of(
                    new JavaParamNode(
                        new JavaTypeVariableReferenceNode(subtype.identifier()),
                        upperCamelCaseToLowerCamelCase(subtype.identifier())
                    )
                )
            ))
            .toList();

        var visitorDeclaration = new JavaInterfaceDeclarationNode(
            List.of("T"),
            "Visitor",
            Optional.empty(),
            visitorMethods
        );

        var acceptDeclaration = new JavaInterfaceMethodDeclarationNode(
            List.of("T"),
            new JavaTypeVariableReferenceNode("T"),
            "accept",
            List.of(
                new JavaParamNode(
                    new JavaParameterizedType(
                        new JavaTypeVariableReferenceNode(visitorDeclaration.name()),
                        List.of(new JavaTypeVariableReferenceNode("T"))
                    ),
                    "visitor"
                )
            )
        );


        return new JavaOrdinaryCompilationUnitNode(
            namespaceToPackage(((InterfaceType) node.type()).namespaceName(), context),
            List.of(),
            new JavaInterfaceDeclarationNode(
                List.of(),
                node.name(),
                Optional.of(permits),
                List.of(acceptDeclaration, visitorDeclaration)
            )
        );
    }

    private static JavaExpressionNode compileIntAdd(TypedIntAddNode node, JavaCodeGeneratorContext context) {
        return new JavaAddNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static JavaExpressionNode compileIntEquals(TypedIntEqualsNode node, JavaCodeGeneratorContext context) {
        return new JavaEqualsNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static JavaExpressionNode compileIntLiteral(TypedIntLiteralNode node) {
        return new JavaIntLiteralNode(node.value());
    }

    private static JavaExpressionNode compileListLiteral(
        TypedListLiteralNode node,
        JavaCodeGeneratorContext context
    ) {
        return new JavaCallNode(
            // TODO: handle fully-qualified references
            new JavaReferenceNode("java.util.List.of"),
            node.elements().stream()
                .map(element -> compileExpression(element, context))
                .toList()
        );
    }

    private static JavaExpressionNode compileLocalReference(TypedLocalReferenceNode node, JavaCodeGeneratorContext context) {
        return new JavaReferenceNode(context.variableName(node.name()));
    }

    private static JavaExpressionNode compileLogicalAnd(TypedLogicalAndNode node, JavaCodeGeneratorContext context) {
        return new JavaLogicalAndNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static JavaExpressionNode compileLogicalNot(TypedLogicalNotNode node, JavaCodeGeneratorContext context) {
        return new JavaLogicalNotNode(compileExpression(node.operand(), context));
    }

    private static JavaExpressionNode compileLogicalOr(TypedLogicalOrNode node, JavaCodeGeneratorContext context) {
        return new JavaLogicalOrNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static JavaExpressionNode compileMemberAccess(TypedMemberAccessNode node, JavaCodeGeneratorContext context) {
        var javaMemberAccess = new JavaMemberAccessNode(
            compileExpression(node.receiver(), context),
            node.memberName()
        );
        if (node.receiver().type() instanceof NamespaceType) {
            return javaMemberAccess;
        } else {
            return new JavaCallNode(
                javaMemberAccess,
                List.of()
            );
        }
    }

    private static JavaExpressionNode compileMemberReference(TypedMemberReferenceNode node) {
        return new JavaCallNode(
            new JavaReferenceNode(node.name()),
            List.of()
        );
    }

    public static List<JavaOrdinaryCompilationUnitNode> compileNamespace(
        TypedNamespaceNode node,
        JavaTargetConfig config,
        SubtypeRelations subtypeRelations
    ) {
        var context = new JavaCodeGeneratorContext(config, subtypeRelations);
        var compilationUnits = new ArrayList<JavaOrdinaryCompilationUnitNode>();
        var classBody = new ArrayList<JavaClassBodyDeclarationNode>();
        var imports = new ArrayList<JavaImportNode>();

        for (var import_ : node.imports()) {
            if (import_.fieldName().isEmpty()) {
                var packageName = namespaceToPackage(import_.namespaceName(), context);
                var className = namespaceNameToClassName(import_.namespaceName(), SourceType.SOURCE);
                imports.add(new JavaImportTypeNode(packageName + "." + className));
                context.renameVariable(import_.variableName(), className);
            }
        }

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
                    classBody.add(compileFunction(functionNode, context));
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
                public Void visit(TypedSingleLineCommentNode node) {
                    // TODO: handle this. Prefix to next compilation unit?
                    return null;
                }

                @Override
                public Void visit(TypedTestNode testNode) {
                    classBody.add(compileTest(testNode, context));
                    return null;
                }

                @Override
                public Void visit(TypedTestSuiteNode node) {
                    classBody.add(compileTestSuite(node, context));
                    return null;
                }
            });
        }

        imports.addAll(context.imports());

        if (!classBody.isEmpty()) {
            var className = namespaceNameToClassName(node.name(), node.sourceType());

            compilationUnits.add(new JavaOrdinaryCompilationUnitNode(
                namespaceToPackage(node.name(), context),
                imports,
                new JavaClassDeclarationNode(List.of(), className, classBody)
            ));
        }

        return compilationUnits;
    }

    private static JavaParamNode compileParam(TypedParamNode node, JavaCodeGeneratorContext context) {
        return new JavaParamNode(compileTypeLevelExpression(node.type(), context), node.name());
    }

    private static JavaClassBodyDeclarationNode compileProperty(
        TypedPropertyNode node,
        JavaCodeGeneratorContext context
    ) {
        return new JavaMethodDeclarationNode(
            List.of(),
            false,
            List.of(),
            compileTypeLevelExpression(node.type(), context),
            node.name(),
            List.of(),
            node.body().stream()
                .map(statement -> compileFunctionStatement(statement, context))
                .toList()
        );
    }

    public static JavaOrdinaryCompilationUnitNode compileRecord(
        TypedRecordNode node,
        JavaCodeGeneratorContext context
    ) {
        var components = node.fields().stream()
            .map(field -> new JavaRecordComponentNode(compileTypeLevelExpression(field.type(), context), field.name()))
            .collect(Collectors.toList());

        var supertypes = context.supertypesOf(node.type());

        var implements_ = supertypes.stream()
            .map(supertype -> new JavaTypeVariableReferenceNode(supertype.identifier()))
            .toList();

        var body = new ArrayList<JavaClassBodyDeclarationNode>();

        for (var declaration : node.body()) {
            body.add(compileRecordBodyDeclaration(declaration, context));
        }

        for (var supertype : supertypes) {
            body.add(new JavaMethodDeclarationNode(
                List.of(),
                false,
                List.of("T"),
                new JavaTypeVariableReferenceNode("T"),
                "accept",
                List.of(
                    new JavaParamNode(
                        new JavaParameterizedType(
                            // TODO: this isn't a full qualified reference
                            new JavaFullyQualifiedTypeReferenceNode(supertype.identifier(), "Visitor"),
                            List.of(new JavaTypeVariableReferenceNode("T"))
                        ),
                        "visitor"
                    )
                ),
                List.of(
                    new JavaReturnNode(
                        new JavaCallNode(
                            new JavaMemberAccessNode(
                                new JavaReferenceNode("visitor"),
                                "visit"
                            ),
                            List.of(new JavaReferenceNode("this"))
                        )
                    )
                )
            ));
        }

        return new JavaOrdinaryCompilationUnitNode(
            namespaceToPackage(node.type().namespaceName(), context),
            List.of(),
            new JavaRecordDeclarationNode(
                node.name(),
                components,
                implements_,
                body
            )
        );
    }

    private static JavaClassBodyDeclarationNode compileRecordBodyDeclaration(
        TypedRecordBodyDeclarationNode node,
        JavaCodeGeneratorContext context
    ) {
        return node.accept(new TypedRecordBodyDeclarationNode.Visitor<JavaClassBodyDeclarationNode>() {
            @Override
            public JavaClassBodyDeclarationNode visit(TypedBlankLineNode node) {
                return compileBlankLine(node, context);
            }

            @Override
            public JavaClassBodyDeclarationNode visit(TypedPropertyNode node) {
                return compileProperty(node, context);
            }

            @Override
            public JavaClassBodyDeclarationNode visit(TypedSingleLineCommentNode node) {
                return compileSingleLineComment(node);
            }
        });
    }

    private static JavaStatementNode compileReturn(TypedReturnNode node, JavaCodeGeneratorContext context) {
        return new JavaReturnNode(compileExpression(node.expression(), context));
    }

    private static JavaSingleLineCommentNode compileSingleLineComment(TypedSingleLineCommentNode node) {
        return new JavaSingleLineCommentNode(node.value());
    }

    private static JavaExpressionNode compileStaticMethodToFunction(
        TypedStaticMethodToFunctionNode node,
        JavaCodeGeneratorContext context
    ) {
        // TODO: test this
        var staticFunctionType = (StaticFunctionType) node.method().type();
        var packageName = namespaceToPackage(staticFunctionType.namespaceName(), context);
        // TODO: sourceType will not always be SOURCE
        var className = namespaceNameToClassName(staticFunctionType.namespaceName(), SourceType.SOURCE);

        return new JavaMethodReferenceStaticNode(
            new JavaFullyQualifiedTypeReferenceNode(packageName, className),
            staticFunctionType.functionName()
        );
    }

    private static JavaExpressionNode compileStringEquals(TypedStringEqualsNode node, JavaCodeGeneratorContext context) {
        return new JavaCallNode(
            new JavaMemberAccessNode(
                compileExpression(node.left(), context),
                "equals"
            ),
            List.of(compileExpression(node.right(), context))
        );
    }

    private static JavaStringLiteralNode compileStringLiteral(TypedStringLiteralNode node) {
        return new JavaStringLiteralNode(node.value());
    }

    private static JavaStatementNode compileSwitch(
        TypedSwitchNode node,
        JavaCodeGeneratorContext context
    ) {
        var expression = node.expression();
        var interfaceType = (InterfaceType) expression.type();

        // TODO: import or fully qualify?
        context.addImportType(typeToJavaTypeName(interfaceType, context));

        var javaCaseReturnType = node.returnType().isPresent()
            ? typeLevelValueToTypeExpression(node.returnType().get(), true, context)
            : new JavaTypeVariableReferenceNode("Void");

        var acceptCall = new JavaCallNode(
            new JavaMemberAccessNode(
                compileExpression(expression, context),
                "accept"
            ),
            List.of(
                new JavaCallNewNode(
                    // TODO: model nested type reference properly
                    new JavaReferenceNode(interfaceType.name() + ".Visitor"),
                    Optional.of(List.of()),
                    List.of(),
                    Optional.of(
                        node.cases().stream()
                            .map(switchCase -> {
                                var caseType = (RecordType) switchCase.type().value();
                                context.addImportType(typeToJavaTypeName(caseType, context));

                                var body = new ArrayList<JavaStatementNode>();
                                for (var statement : switchCase.body()) {
                                    body.add(compileFunctionStatement(statement, context));
                                }
                                if (!node.returns()) {
                                    body.add(new JavaReturnNode(new JavaReferenceNode("null")));
                                }

                                return new JavaMethodDeclarationNode(
                                    List.of(
                                        new JavaMarkerAnnotationNode(new JavaTypeVariableReferenceNode("Override"))
                                    ),
                                    false,
                                    List.of(),
                                    javaCaseReturnType,
                                    "visit",
                                    List.of(
                                        new JavaParamNode(new JavaTypeVariableReferenceNode(caseType.name()), node.expression().name())
                                    ),
                                    body
                                );
                            })
                            .toList()
                    )
                )
            )
        );

        if (node.returns()) {
            return new JavaReturnNode(acceptCall);
        } else {
            return new JavaExpressionStatementNode(acceptCall);
        }
    }

    public static JavaClassBodyDeclarationNode compileTest(TypedTestNode node, JavaCodeGeneratorContext context) {
        var method = JavaMethodDeclarationNode.builder()
            .addAnnotation(Java.annotation(Java.fullyQualifiedTypeReference("org.junit.jupiter.api", "Test")))
            .addAnnotation(Java.annotation(
                Java.fullyQualifiedTypeReference("org.junit.jupiter.api", "DisplayName"),
                Java.string(node.name())
            ))
            .isStatic(false)
            .name(JavaTestNames.generateTestName(node.name()));

        for (var statement : node.body()) {
            method = method.addBodyStatement(compileFunctionStatement(statement, context));
        }

        return method.build();
    }

    public static JavaClassBodyDeclarationNode compileTestSuite(
        TypedTestSuiteNode node,
        JavaCodeGeneratorContext context
    ) {
        return new JavaClassDeclarationNode(
            List.of(
                Java.annotation(Java.fullyQualifiedTypeReference("org.junit.jupiter.api", "Nested"))
            ),
            JavaTestNames.generateTestSuiteName(node.name()),
            node.body().stream()
                // TODO: handle statements that aren't tests nor test suites
                .map(statement -> {
                    if (statement instanceof TypedBlankLineNode blankLineNode) {
                        return compileBlankLine(blankLineNode, context);
                    } else if (statement instanceof TypedTestNode testNode) {
                        return compileTest(testNode, context);
                    } else if (statement instanceof TypedTestSuiteNode testSuiteNode) {
                        return compileTestSuite(testSuiteNode, context);
                    } else {
                        throw new UnsupportedOperationException("TODO");
                    }
                })
                .toList()
        );
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
                        .map(arg -> {
                            var argTypeExpression = compileTypeLevelExpression(arg.type(), context);
                            if (arg.isCovariant() && context.isSealedInterfaceType((Type) arg.type().value())) {
                                return new JavaExtendsTypeNode(new JavaWildcardTypeNode(), argTypeExpression);
                            } else {
                                return argTypeExpression;
                            }
                        })
                        .toList()
                );
            }

            @Override
            public JavaTypeExpressionNode visit(TypedTypeLevelReferenceNode node) {
                var builtinReference = builtinReference(node.value(), false);

                if (builtinReference.isPresent()) {
                    return builtinReference.get();
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

    public static JavaTypeExpressionNode typeLevelValueToTypeExpression(
        TypeLevelValue value,
        boolean isGeneric,
        JavaCodeGeneratorContext context
    ) {
        var builtinReference = builtinReference(value, isGeneric);

        if (builtinReference.isPresent()) {
            return builtinReference.get();
        } else if (value instanceof InterfaceType interfaceType) {
            return new JavaFullyQualifiedTypeReferenceNode(
                namespaceToPackage(interfaceType.namespaceName(), context),
                interfaceType.name()
            );
        } else if (value instanceof RecordType recordType) {
            return new JavaFullyQualifiedTypeReferenceNode(
                namespaceToPackage(recordType.namespaceName(), context),
                recordType.name()
            );
        } else if (value instanceof ConstructedType constructedType) {
            return new JavaParameterizedType(
                typeLevelValueToTypeExpression(constructedType.constructor(), isGeneric, context),
                constructedType.args().stream()
                    .map(arg -> typeLevelValueToTypeExpression(arg, true, context))
                    .toList()
            );
        } else {
            throw new UnsupportedOperationException("TODO: " + value);
        }
    }

    public static Optional<JavaTypeExpressionNode> builtinReference(TypeLevelValue value, boolean isGeneric) {
        if (value == BoolType.INSTANCE) {
            return Optional.of(new JavaTypeVariableReferenceNode(isGeneric ? "Boolean": "boolean"));
        } else if (value == IntType.INSTANCE) {
            return Optional.of(new JavaTypeVariableReferenceNode(isGeneric ? "Integer" : "int"));
        } else if (value == StringType.INSTANCE) {
            return Optional.of(new JavaTypeVariableReferenceNode("String"));
        } else if (value == Types.LIST_CONSTRUCTOR) {
            return Optional.of(new JavaFullyQualifiedTypeReferenceNode("java.util", "List"));
        } else if (value == Types.OPTION_CONSTRUCTOR) {
            return Optional.of(new JavaFullyQualifiedTypeReferenceNode("java.util", "Optional"));
        } else {
            return Optional.empty();
        }
    }

    private static String typeToJavaTypeName(InterfaceType interfaceType, JavaCodeGeneratorContext context) {
        return typeToJavaTypeName(interfaceType.namespaceName(), interfaceType.name(), context);
    }

    private static String typeToJavaTypeName(RecordType recordType, JavaCodeGeneratorContext context) {
        return typeToJavaTypeName(recordType.namespaceName(), recordType.name(), context);
    }

    private static String typeToJavaTypeName(NamespaceName namespaceName, String name, JavaCodeGeneratorContext context) {
        return namespaceToPackage(namespaceName, context) + "." + name;
    }

    private static String namespaceNameToClassName(NamespaceName name, SourceType sourceType) {
        var classNameSuffix = sourceType.equals(SourceType.TEST) ? "Tests" : "";
        return lowerCamelCaseToUpperCamelCase(last(name.parts())) + classNameSuffix;
    }
}
