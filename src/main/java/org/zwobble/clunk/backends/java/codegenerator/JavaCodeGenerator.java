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
import java.util.stream.Stream;

import static org.zwobble.clunk.backends.CaseConverter.lowerCamelCaseToUpperCamelCase;
import static org.zwobble.clunk.backends.CaseConverter.upperCamelCaseToLowerCamelCase;
import static org.zwobble.clunk.util.Lists.last;

public class JavaCodeGenerator {
    static List<JavaExpressionNode> compileArgs(TypedArgsNode args, JavaCodeGeneratorContext context) {
        return Stream.concat(args.positional().stream(), args.named().stream().map(arg -> arg.expression()))
            .map(arg -> compileExpression(arg, context))
            .toList();
    }

    private static JavaBlankLineNode compileBlankLine(TypedBlankLineNode node, JavaCodeGeneratorContext context) {
        return new JavaBlankLineNode();
    }

    public static List<JavaStatementNode> compileBlock(
        List<TypedFunctionStatementNode> nodes,
        JavaCodeGeneratorContext context
    ) {
        var blockContext = context.enterBlock();
        return nodes.stream().flatMap(statement -> compileFunctionStatement(statement, blockContext).stream()).toList();
    }

    private static JavaExpressionNode compileBoolLiteral(TypedBoolLiteralNode node) {
        return new JavaBoolLiteralNode(node.value());
    }

    private static JavaExpressionNode compileCallConstructor(TypedCallConstructorNode node, JavaCodeGeneratorContext context) {
        var javaReceiver = compileExpression(node.receiver(), context);
        var javaArgs = compileArgs(node.args(), context);

        var macroResult = JavaMacros.compileConstructorCall(node.type(), javaArgs, context);
        if (macroResult.isPresent()) {
            return macroResult.get();
        } else {
            // TODO: handle not a record type
            var recordType = (RecordType) node.type();
            // TODO: handle conflicts
            context.addImportType(typeToJavaTypeName(recordType, context));
            return new JavaCallNewNode(new JavaReferenceNode(recordType.name()), Optional.empty(), javaArgs, Optional.empty());
        }
    }

    private static JavaExpressionNode compileCallMethod(TypedCallMethodNode node, JavaCodeGeneratorContext context) {
        var receiver = node.receiver();
        var javaReceiver = receiver.map(r -> compileExpression(r, context));
        var javaArgs = compileArgs(node.args(), context);

        if (receiver.isPresent()) {
            var macroResult = JavaMacros.compileMethodCall(
                receiver.get().type(),
                javaReceiver.get(),
                node.methodName(),
                javaArgs
            );
            if (macroResult.isPresent()) {
                return macroResult.get();
            }
        }

        return new JavaCallNode(
            javaReceiver.isPresent()
                ? new JavaMemberAccessNode(
                    javaReceiver.get(),
                    node.methodName()
                )
                : new JavaReferenceNode(node.methodName()),
            javaArgs
        );
    }

    private static JavaExpressionNode compileCallStaticFunction(TypedCallStaticFunctionNode node, JavaCodeGeneratorContext context) {
        var macro = JavaMacros.lookupStaticFunctionMacro(node.receiverType());

        if (macro.isPresent()) {
            return macro.get().compileCall(
                compileArgs(node.args(), context),
                context
            );
        } else {
            return new JavaCallNode(
                compileExpression(node.receiver(), context),
                compileArgs(node.args(), context)
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
        var packageDeclaration = namespaceToPackage(node.type().namespaceId(), context);
        var typeDeclaration = new JavaEnumDeclarationNode(
            node.type().name(),
            node.type().members()
        );
        return new JavaOrdinaryCompilationUnitNode(packageDeclaration, context.generateImports(), typeDeclaration);
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
            public JavaExpressionNode visit(TypedIntNotEqualNode node) {
                return compileIntNotEqual(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedListComprehensionNode node) {
                return compileListComprehension(node, context);
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
            public JavaExpressionNode visit(TypedMapLiteralNode node) {
                return compileMapLiteral(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedMemberAccessNode node) {
                return compileMemberAccess(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedMemberDefinitionReferenceNode node) {
                return compileMemberDefinitionReference(node, context);
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

            @Override
            public JavaExpressionNode visit(TypedStringNotEqualNode node) {
                return compileStringNotEqual(node, context);
            }

            @Override
            public JavaExpressionNode visit(TypedStructuredEqualsNode node) {
                return compileStructuredEquals(node, context);
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
            compileBlock(node.body(), context)
        );
    }

    public static JavaClassBodyDeclarationNode compileFunction(
        TypedFunctionNode node,
        boolean isStatic,
        JavaCodeGeneratorContext context
    ) {
        return new JavaMethodDeclarationNode(
            List.of(),
            isStatic,
            List.of(),
            compileTypeLevelExpression(node.returnType(), context),
            node.name(),
            Stream.concat(node.params().positional().stream(), node.params().named().stream())
                .map(param -> compileParam(param, context))
                .toList(),
            compileBlock(node.body(), context)
        );
    }

    public static List<JavaStatementNode> compileFunctionStatement(TypedFunctionStatementNode node, JavaCodeGeneratorContext context) {
        return node.accept(new TypedFunctionStatementNode.Visitor<List<JavaStatementNode>>() {
            @Override
            public List<JavaStatementNode> visit(TypedBlankLineNode node) {
                return List.of(compileBlankLine(node, context));
            }

            @Override
            public List<JavaStatementNode> visit(TypedExpressionStatementNode node) {
                return List.of(compileExpressionStatement(node, context));
            }

            @Override
            public List<JavaStatementNode> visit(TypedForEachNode node) {
                return List.of(compileForEach(node, context));
            }

            @Override
            public List<JavaStatementNode> visit(TypedIfStatementNode node) {
                return List.of(compileIfStatement(node, context));
            }

            @Override
            public List<JavaStatementNode> visit(TypedReturnNode node) {
                return List.of(compileReturn(node, context));
            }

            @Override
            public List<JavaStatementNode> visit(TypedSingleLineCommentNode node) {
                return List.of(compileSingleLineComment(node));
            }

            @Override
            public List<JavaStatementNode> visit(TypedSwitchNode node) {
                return List.of(compileSwitch(node, context));
            }

            @Override
            public List<JavaStatementNode> visit(TypedTypeNarrowNode node) {
                return List.of(compileTypeNarrow(node, context));
            }

            @Override
            public List<JavaStatementNode> visit(TypedVarNode node) {
                return List.of(compileVar(node, context));
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
                    compileBlock(conditionalBranch.body(), context)
                ))
                .toList(),
            compileBlock(node.elseBody(), context)
        );
    }

    private static List<JavaImportNode> compileImport(
        TypedImportNode import_,
        JavaCodeGeneratorContext context
    ) {
        var macro = JavaMacros.lookupStaticFunctionMacro(import_.type());
        var packageName = namespaceToPackage(import_.namespaceId(), context);
        if (macro.isPresent()) {
            return List.of();
        } else if (import_.fieldName().isEmpty()) {
            var className = namespaceIdToClassName(import_.namespaceId());
            context.renameVariable(import_.variableName(), className);
            return List.of(new JavaImportTypeNode(packageName + "." + className));
        } else if (Types.isMetaType(import_.type())) {
            return List.of(new JavaImportTypeNode(packageName + "." + import_.fieldName().get()));
        } else {
            var className = namespaceIdToClassName(import_.namespaceId());
            var fullyQualifiedClassName = packageName + "." + className;
            return List.of(new JavaImportStaticNode(fullyQualifiedClassName, import_.fieldName().get()));
        }
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
            compileTypeLevelExpression(node.typeExpression(), context),
            Optional.empty()
        );
    }

    public static JavaOrdinaryCompilationUnitNode compileInterface(
        TypedInterfaceNode node,
        JavaCodeGeneratorContext context
    ) {
        if (node.type().isSealed()) {
            return compiledInterfaceSealed(node, context);
        } else {
            return compileInterfaceUnsealed(node, context);
        }
    }

    private static JavaOrdinaryCompilationUnitNode compiledInterfaceSealed(TypedInterfaceNode node, JavaCodeGeneratorContext context) {
        var permits = context.sealedInterfaceCases(node.type()).stream()
            .map(subtype -> new JavaTypeVariableReferenceNode(subtype.name()))
            .toList();

        var visitorMethods = context.sealedInterfaceCases(node.type()).stream()
            .map(subtype -> new JavaInterfaceMethodDeclarationNode(
                List.of(),
                new JavaTypeVariableReferenceNode("T"),
                "visit",
                List.of(
                    new JavaParamNode(
                        new JavaTypeVariableReferenceNode(subtype.name()),
                        upperCamelCaseToLowerCamelCase(subtype.name())
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
            namespaceToPackage(node.type().namespaceId(), context),
            context.generateImports(),
            new JavaInterfaceDeclarationNode(
                List.of(),
                node.name(),
                Optional.of(permits),
                List.of(acceptDeclaration, visitorDeclaration)
            )
        );
    }

    private static JavaOrdinaryCompilationUnitNode compileInterfaceUnsealed(TypedInterfaceNode node, JavaCodeGeneratorContext context) {
        return new JavaOrdinaryCompilationUnitNode(
            namespaceToPackage(node.type().namespaceId(), context),
            context.generateImports(),
            new JavaInterfaceDeclarationNode(
                List.of(),
                node.name(),
                Optional.empty(),
                List.of()
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

    private static JavaExpressionNode compileIntNotEqual(
        TypedIntNotEqualNode node,
        JavaCodeGeneratorContext context
    ) {
        return new JavaNotEqualNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static JavaExpressionNode compileListComprehension(
        TypedListComprehensionNode node,
        JavaCodeGeneratorContext context
    ) {
        var result = compileExpression(node.yield(), context);

        for (var i = node.forClauses().size() - 1; i >= 0; i--) {
            var forClause = node.forClauses().get(i);

            var iterable = compileExpression(forClause.iterable(), context);
            var stream = new JavaCallNode(
                new JavaMemberAccessNode(iterable, "stream"),
                List.of()
            );
            for (var ifClause : forClause.ifClauses()) {
                if (ifClause.narrowedTargetType().isPresent()) {
                    stream = new JavaCallNode(
                        new JavaMemberAccessNode(stream, "flatMap"),
                        List.of(
                            new JavaLambdaExpressionNode(
                                List.of(forClause.targetName()),
                                new JavaConditionalNode(
                                    compileExpression(ifClause.condition(), context),
                                    // TODO: represent fully qualified static method reference properly
                                    new JavaCallNode(
                                        new JavaReferenceNode("java.util.stream.Stream.of"),
                                        List.of(
                                            new JavaCastNode(
                                                typeLevelValueToTypeExpression(ifClause.narrowedTargetType().get(), false, context),
                                                new JavaReferenceNode(forClause.targetName())
                                            )
                                        )
                                    ),
                                    // TODO: add proper Java null node
                                    new JavaReferenceNode("null")
                                )
                            )
                        )
                    );
                } else {
                    stream = new JavaCallNode(
                        new JavaMemberAccessNode(stream, "filter"),
                        List.of(
                            new JavaLambdaExpressionNode(
                                List.of(forClause.targetName()),
                                compileExpression(ifClause.condition(), context)
                            )
                        )
                    );
                }
            }

            var isLast = i == node.forClauses().size() - 1;

            if (isLast && result instanceof JavaReferenceNode resultRef && resultRef.name().equals(forClause.targetName())) {
                result = stream;
            } else {
                var mapMethodName = isLast ? "map" : "flatMap";
                result = new JavaCallNode(
                    new JavaMemberAccessNode(stream, mapMethodName),
                    List.of(
                        new JavaLambdaExpressionNode(
                            List.of(forClause.targetName()),
                            result
                        )
                    )
                );
            }
        }

        return new JavaCallNode(
            new JavaMemberAccessNode(result, "toList"),
            List.of()
        );
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
        return localReference(node.name(), context);
    }

    private static JavaExpressionNode localReference(String name, JavaCodeGeneratorContext context) {
        return new JavaReferenceNode(context.variableName(name));
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

    private static JavaExpressionNode compileMapLiteral(
        TypedMapLiteralNode node,
        JavaCodeGeneratorContext context
    ) {
        return new JavaCallStaticNode(
            // TODO: handle fully-qualified references
            new JavaTypeVariableReferenceNode("java.util.Map.ofEntries"),
            node.entries().stream()
                .<JavaExpressionNode>map(entry -> new JavaCallStaticNode(
                    new JavaTypeVariableReferenceNode("java.util.Map.entry"),
                    List.of(
                        compileExpression(entry.key(), context),
                        compileExpression(entry.value(), context)
                    )
                ))
                .toList()
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

    private static JavaExpressionNode compileMemberDefinitionReference(
        TypedMemberDefinitionReferenceNode node,
        JavaCodeGeneratorContext context
    ) {
        // TODO: compile receiver expression rather than the type
        var receiverMetaType = (TypeLevelValueType) node.receiver().type();
        var receiver = typeLevelValueToTypeExpression(receiverMetaType.value(), false, context);
        return new JavaMethodReferenceStaticNode(
            receiver,
            node.memberName()
        );
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
        var baseContext = new JavaCodeGeneratorContext(config, subtypeRelations);
        var compilationUnits = new ArrayList<JavaOrdinaryCompilationUnitNode>();
        var classBody = new ArrayList<JavaClassBodyDeclarationNode>();

        for (var import_ : node.imports()) {
            baseContext.addImports(compileImport(import_, baseContext));
        }

        var topLevelContext = baseContext.enterCompilationUnit();

        for (var statement : node.statements()) {
            statement.accept(new TypedNamespaceStatementNode.Visitor<Void>() {
                @Override
                public Void visit(TypedBlankLineNode node) {
                    return null;
                }

                @Override
                public Void visit(TypedEnumNode node) {
                    compilationUnits.add(compileEnum(node, baseContext.enterCompilationUnit()));
                    return null;
                }

                @Override
                public Void visit(TypedFunctionNode functionNode) {
                    classBody.add(compileFunction(functionNode, true, topLevelContext));
                    return null;
                }

                @Override
                public Void visit(TypedInterfaceNode interfaceNode) {
                    compilationUnits.add(compileInterface(interfaceNode, baseContext.enterCompilationUnit()));
                    return null;
                }

                @Override
                public Void visit(TypedRecordNode recordNode) {
                    compilationUnits.add(compileRecord(recordNode, baseContext.enterCompilationUnit()));
                    return null;
                }

                @Override
                public Void visit(TypedSingleLineCommentNode node) {
                    // TODO: handle this. Prefix to next compilation unit?
                    return null;
                }

                @Override
                public Void visit(TypedTestNode testNode) {
                    classBody.add(compileTest(testNode, topLevelContext));
                    return null;
                }

                @Override
                public Void visit(TypedTestSuiteNode node) {
                    classBody.add(compileTestSuite(node, topLevelContext));
                    return null;
                }
            });
        }

        if (!classBody.isEmpty()) {
            var className = namespaceIdToClassName(node.id());

            compilationUnits.add(new JavaOrdinaryCompilationUnitNode(
                namespaceToPackage(node.id(), topLevelContext),
                topLevelContext.generateImports(),
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
            compileBlock(node.body(), context)
        );
    }

    public static JavaOrdinaryCompilationUnitNode compileRecord(
        TypedRecordNode node,
        JavaCodeGeneratorContext context
    ) {
        var components = node.fields().stream()
            .map(field -> new JavaRecordComponentNode(compileTypeLevelExpression(field.type(), context), field.name()))
            .collect(Collectors.toList());

        var extendedTypes = context.extendedTypes(node.type());

        var implements_ = extendedTypes.stream()
            .map(supertype -> typeLevelValueToTypeExpression(supertype, false, context))
            .toList();

        var body = new ArrayList<JavaClassBodyDeclarationNode>();

        for (var declaration : node.body()) {
            body.add(compileRecordBodyDeclaration(declaration, context));
        }

        for (var supertype : extendedTypes) {
            if (Types.isSealedInterfaceType(supertype)) {
                var acceptMethod = generateAcceptMethod(supertype, context);
                body.add(acceptMethod);
            }
        }

        return new JavaOrdinaryCompilationUnitNode(
            namespaceToPackage(node.type().namespaceId(), context),
            context.generateImports(),
            new JavaRecordDeclarationNode(
                node.name(),
                components,
                implements_,
                body
            )
        );
    }

    private static JavaMethodDeclarationNode generateAcceptMethod(StructuredType supertype, JavaCodeGeneratorContext context) {
        // TODO: don't rely on this being a JavaFullyQualifiedTypeReferenceNode
        var supertypeTypeExpression = (JavaFullyQualifiedTypeReferenceNode) typeLevelValueToTypeExpression(supertype, false, context);
        // TODO: make this an inner class reference
        var visitorTypeExpression = new JavaFullyQualifiedTypeReferenceNode(
            supertypeTypeExpression.packageName() + "." + supertypeTypeExpression.typeName(),
            "Visitor"
        );
        return new JavaMethodDeclarationNode(
            List.of(),
            false,
            List.of("T"),
            new JavaTypeVariableReferenceNode("T"),
            "accept",
            List.of(
                new JavaParamNode(
                    new JavaParameterizedType(
                        visitorTypeExpression,
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
            public JavaClassBodyDeclarationNode visit(TypedFunctionNode node) {
                return compileFunction(node, false, context);
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
        var packageName = namespaceToPackage(staticFunctionType.namespaceId(), context);
        var className = namespaceIdToClassName(staticFunctionType.namespaceId());

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

    private static JavaExpressionNode compileStringNotEqual(
        TypedStringNotEqualNode node,
        JavaCodeGeneratorContext context
    ) {
        return new JavaLogicalNotNode(new JavaCallNode(
            new JavaMemberAccessNode(
                compileExpression(node.left(), context),
                "equals"
            ),
            List.of(compileExpression(node.right(), context))
        ));
    }

    private static JavaExpressionNode compileStructuredEquals(
        TypedStructuredEqualsNode node,
        JavaCodeGeneratorContext context
    ) {
        return new JavaCallNode(
            new JavaMemberAccessNode(
                compileExpression(node.left(), context),
                "equals"
            ),
            List.of(compileExpression(node.right(), context))
        );
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
                                body.addAll(compileBlock(switchCase.body(), context));
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

        for (var statement : compileBlock(node.body(), context)) {
            method = method.addBodyStatement(statement);
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
                            // TODO: should probably just check whether type is final or not
                            if (arg.isCovariant() && context.isInterfaceType((Type) arg.type().value())) {
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

    private static JavaStatementNode compileTypeNarrow(
        TypedTypeNarrowNode node,
        JavaCodeGeneratorContext context
    ) {
        var narrowedName = node.variableName() + "_" + node.type().name();
        var result = new JavaVariableDeclarationNode(
            narrowedName,
            new JavaCastNode(
                typeLevelValueToTypeExpression(node.type(), false, context),
                localReference(node.variableName(), context)
            )
        );

        context.renameVariable(node.variableName(), narrowedName);

        return result;
    }

    private static JavaStatementNode compileVar(TypedVarNode node, JavaCodeGeneratorContext context) {
        return new JavaVariableDeclarationNode(node.name(), compileExpression(node.expression(), context));
    }

    private static String namespaceToPackage(NamespaceId namespaceId, JavaCodeGeneratorContext context) {
        return context.packagePrefix() + String.join(".", namespaceId.name().parts());
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
                namespaceToPackage(interfaceType.namespaceId(), context),
                interfaceType.name()
            );
        } else if (value instanceof RecordType recordType) {
            return new JavaFullyQualifiedTypeReferenceNode(
                namespaceToPackage(recordType.namespaceId(), context),
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
        } else if (value == Types.OPTION_CONSTRUCTOR) {
            return Optional.of(new JavaFullyQualifiedTypeReferenceNode("java.util", "Optional"));
        } else if (value instanceof Type type) {
            return JavaMacros.compileTypeReference(type);
        } else if (value instanceof TypeConstructor typeConstructor) {
            return JavaMacros.compileTypeConstructorReference(typeConstructor);
        } else {
            return Optional.empty();
        }
    }

    private static String typeToJavaTypeName(InterfaceType interfaceType, JavaCodeGeneratorContext context) {
        return typeToJavaTypeName(interfaceType.namespaceId(), interfaceType.name(), context);
    }

    private static String typeToJavaTypeName(RecordType recordType, JavaCodeGeneratorContext context) {
        return typeToJavaTypeName(recordType.namespaceId(), recordType.name(), context);
    }

    private static String typeToJavaTypeName(NamespaceId namespaceId, String name, JavaCodeGeneratorContext context) {
        return namespaceToPackage(namespaceId, context) + "." + name;
    }

    private static String namespaceIdToClassName(NamespaceId id) {
        var classNameSuffix = id.sourceType().equals(SourceType.TEST) ? "Tests" : "";
        return lowerCamelCaseToUpperCamelCase(last(id.name().parts())) + classNameSuffix;
    }
}
