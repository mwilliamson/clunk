package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.typescript.ast.*;
import org.zwobble.clunk.types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.zwobble.clunk.util.Lists.last;

public class TypeScriptCodeGenerator {
    private static List<TypeScriptExpressionNode> compileArgs(
        List<TypedExpressionNode> positionalArgs,
        TypeScriptCodeGeneratorContext context
    ) {
        return positionalArgs.stream()
            .map(arg -> compileExpression(arg, context))
            .toList();
    }

    private static TypeScriptBlankLineNode compileBlankLine(TypedBlankLineNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptBlankLineNode();
    }

    private static TypeScriptExpressionNode compileBoolLiteral(TypedBoolLiteralNode node) {
        return new TypeScriptBoolLiteralNode(node.value());
    }

    private static TypeScriptExpressionNode compileCallConstructor(TypedCallConstructorNode node, TypeScriptCodeGeneratorContext context) {
        var classMacro = TypeScriptMacros.lookupClassMacro(node.type());
        if (classMacro.isPresent()) {
            Optional<List<TypeScriptExpressionNode>> typeArgs = node.typeArgs().isPresent()
                ? Optional.of(node.typeArgs().get().stream().map(typeArg -> compileTypeLevelExpression(typeArg)).toList())
                : Optional.empty();

            return classMacro.get().compileConstructorCall(
                typeArgs,
                compileArgs(node.positionalArgs(), context)
            );
        } else {
            return new TypeScriptCallNewNode(
                compileExpression(node.receiver(), context),
                List.of(),
                compileArgs(node.positionalArgs(), context)
            );
        }
    }

    private static TypeScriptExpressionNode compileCallMethod(TypedCallMethodNode node, TypeScriptCodeGeneratorContext context) {
        var receiver = node.receiver();
        if (receiver.isPresent()) {
            var macro = TypeScriptMacros.lookupClassMacro(receiver.get().type());

            if (macro.isPresent()) {
                return macro.get().compileMethodCall(
                    compileExpression(receiver.get(), context),
                    node.methodName(),
                    compileArgs(node.positionalArgs(), context),
                    context
                );
            }
        }

        return new TypeScriptCallNode(
            new TypeScriptPropertyAccessNode(
                receiver.map(r -> compileExpression(r, context)).orElse(new TypeScriptReferenceNode("this")),
                node.methodName()
            ),
            compileArgs(node.positionalArgs(), context)
        );
    }

    private static TypeScriptExpressionNode compileCallStaticFunction(TypedCallStaticFunctionNode node, TypeScriptCodeGeneratorContext context) {
        var macro = TypeScriptMacros.lookupStaticFunctionMacro(node.receiverType());

        if (macro.isPresent()) {
            return new TypeScriptCallNode(
                macro.get().compileReceiver(context),
                compileArgs(node.positionalArgs(), context)
            );
        } else {
            return new TypeScriptCallNode(
                compileExpression(node.receiver(), context),
                compileArgs(node.positionalArgs(), context)
            );
        }
    }

    private static TypeScriptExpressionNode compileCastUnsafe(
        TypedCastUnsafeNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptCastNode(
            compileExpression(node.expression(), context),
            compileTypeLevelExpression(node.typeExpression())
        );
    }

    private static TypeScriptStatementNode compileEnum(TypedEnumNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptEnumDeclarationNode(
            node.type().name(),
            node.type().members()
        );
    }

    public static TypeScriptExpressionNode compileExpression(TypedExpressionNode node, TypeScriptCodeGeneratorContext context) {
        return node.accept(new TypedExpressionNode.Visitor<TypeScriptExpressionNode>() {
            @Override
            public TypeScriptExpressionNode visit(TypedBoolLiteralNode node) {
                return compileBoolLiteral(node);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedCallConstructorNode node) {
                return compileCallConstructor(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedCallMethodNode node) {
                return compileCallMethod(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedCallStaticFunctionNode node) {
                return compileCallStaticFunction(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedCastUnsafeNode node) {
                return compileCastUnsafe(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedIndexNode node) {
                return compileIndex(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedInstanceOfNode node) {
                return compileInstanceOf(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedIntAddNode node) {
                return compileIntAdd(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedIntEqualsNode node) {
                return compileIntEquals(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedIntLiteralNode node) {
                return compileIntLiteral(node);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedIntNotEqualNode node) {
                return compileIntNotEqual(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedListLiteralNode node) {
                return compileListLiteral(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedLocalReferenceNode node) {
                return compileLocalReference(node);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedLogicalAndNode node) {
                return compileLogicalAnd(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedLogicalNotNode node) {
                return compileLogicalNot(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedLogicalOrNode node) {
                return compileLogicalOr(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedMapLiteralNode node) {
                return compileMapLiteral(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedMemberAccessNode node) {
                return compileMemberAccess(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedMemberReferenceNode node) {
                return compileMemberReference(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedStaticMethodToFunctionNode node) {
                return compileExpression(node.method(), context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedStringEqualsNode node) {
                return compileStringEquals(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedStringLiteralNode node) {
                return compileStringLiteral(node);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedStringNotEqualNode node) {
                return compileStringNotEqual(node, context);
            }
        });
    }

    private static TypeScriptStatementNode compileExpressionStatement(
        TypedExpressionStatementNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptExpressionStatementNode(compileExpression(node.expression(), context));
    }

    private static TypeScriptStatementNode compileForEach(
        TypedForEachNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptForOfNode(
            node.targetName(),
            compileExpression(node.iterable(), context),
            compileFunctionStatements(node.body(), context)
        );
    }

    private static TypeScriptFunctionDeclarationNode compileFunction(TypedFunctionNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptFunctionDeclarationNode(
            node.name(),
            node.params().stream().map(param -> compileParam(param)).toList(),
            compileTypeLevelExpression(node.returnType()),
            compileFunctionStatements(node.body(), context)
        );
    }

    public static List<TypeScriptStatementNode> compileFunctionStatement(
        TypedFunctionStatementNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return node.accept(new TypedFunctionStatementNode.Visitor<>() {
            @Override
            public List<TypeScriptStatementNode> visit(TypedBlankLineNode node) {
                return List.of(compileBlankLine(node, context));
            }

            @Override
            public List<TypeScriptStatementNode> visit(TypedExpressionStatementNode node) {
                return List.of(compileExpressionStatement(node, context));
            }

            @Override
            public List<TypeScriptStatementNode> visit(TypedForEachNode node) {
                return List.of(compileForEach(node, context));
            }

            @Override
            public List<TypeScriptStatementNode> visit(TypedIfStatementNode node) {
                return List.of(compileIfStatement(node, context));
            }

            @Override
            public List<TypeScriptStatementNode> visit(TypedReturnNode node) {
                return List.of(compileReturn(node, context));
            }

            @Override
            public List<TypeScriptStatementNode> visit(TypedSingleLineCommentNode node) {
                return List.of(compileSingleLineComment(node));
            }

            @Override
            public List<TypeScriptStatementNode> visit(TypedSwitchNode node) {
                return List.of(compileSwitch(node, context));
            }

            @Override
            public List<TypeScriptStatementNode> visit(TypedTypeNarrowNode node) {
                return List.of();
            }

            @Override
            public List<TypeScriptStatementNode> visit(TypedVarNode node) {
                return List.of(compileVar(node, context));
            }
        });
    }

    private static List<TypeScriptStatementNode> compileFunctionStatements(
        List<TypedFunctionStatementNode> nodes,
        TypeScriptCodeGeneratorContext context
    ) {
        return nodes.stream().flatMap(statement -> compileFunctionStatement(statement, context).stream()).toList();
    }

    private static TypeScriptStatementNode compileIfStatement(
        TypedIfStatementNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptIfStatementNode(
            node.conditionalBranches().stream()
                .map(conditionalBranch -> new TypeScriptConditionalBranchNode(
                    compileExpression(conditionalBranch.condition(), context),
                    compileFunctionStatements(conditionalBranch.body(), context)
                ))
                .toList(),
            compileFunctionStatements(node.elseBody(), context)
        );
    }

    private static List<TypeScriptStatementNode> compileImport(TypedImportNode import_, NamespaceName importingNamespaceName) {
        var macro = TypeScriptMacros.lookupStaticFunctionMacro(import_.type());
        if (macro.isPresent()) {
            return List.of();
        }

        var modulePath = namespaceNameToModulePath(import_.namespaceName(), importingNamespaceName);
        if (import_.fieldName().isPresent()) {
            var exportName = import_.fieldName().get();
            return List.of(new TypeScriptImportNamedNode(
                modulePath,
                List.of(new TypeScriptImportNamedMemberNode(exportName, exportName))
            ));
        } else {
            return List.of(new TypeScriptImportNamespaceNode(
                modulePath,
                last(import_.namespaceName().parts())
            ));
        }
    }

    private static String namespaceNameToModulePath(NamespaceName importedNamespace, NamespaceName importingNamespace) {
        var path = new ArrayList<String>();

        var currentNamespaceParts = importingNamespace.parts().subList(0, importingNamespace.parts().size() - 1);
        while (!currentNamespaceParts.equals(importedNamespace.parts().subList(0, currentNamespaceParts.size()))) {
            currentNamespaceParts = currentNamespaceParts.subList(0, currentNamespaceParts.size() - 1);
            path.add("..");
        }

        if (path.size() == 0) {
            path.add(".");
        }

        path.addAll(importedNamespace.parts().subList(
            currentNamespaceParts.size(),
            importedNamespace.parts().size()
        ));

        return String.join("/", path);
    }

    private static TypeScriptExpressionNode compileIndex(
        TypedIndexNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptIndexNode(
            compileExpression(node.receiver(), context),
            compileExpression(node.index(), context)
        );
    }

    private static TypeScriptExpressionNode compileInstanceOf(
        TypedInstanceOfNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        var typeExpressionType = (RecordType) node.typeExpression().value();

        return new TypeScriptStrictEqualsNode(
            new TypeScriptPropertyAccessNode(compileExpression(node.expression(), context), "type"),
            new TypeScriptStringLiteralNode(typeExpressionType.name())
        );
    }

    private static TypeScriptStatementNode compileInterface(
        TypedInterfaceNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        if (node.type().isSealed()) {
            return compileInterfaceSealed(node, context);
        } else {
            return compileInterfaceUnsealed(node, context);
        }
    }

    private static TypeScriptTypeDeclarationNode compileInterfaceSealed(TypedInterfaceNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptTypeDeclarationNode(
            node.name(),
            new TypeScriptUnionNode(
                context.sealedInterfaceCases(node.type()).stream()
                    .map(subtype -> new TypeScriptReferenceNode(subtype.name()))
                    .toList()
            )
        );
    }

    private static TypeScriptStatementNode compileInterfaceUnsealed(TypedInterfaceNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptInterfaceDeclarationNode(
            node.name(),
            List.of()
        );
    }

    private static TypeScriptExpressionNode compileIntAdd(TypedIntAddNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptAddNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static TypeScriptExpressionNode compileIntEquals(TypedIntEqualsNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptStrictEqualsNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static TypeScriptExpressionNode compileIntLiteral(TypedIntLiteralNode node) {
        return new TypeScriptNumberLiteralNode(node.value());
    }

    private static TypeScriptExpressionNode compileIntNotEqual(
        TypedIntNotEqualNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptStrictNotEqualNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static TypeScriptExpressionNode compileListLiteral(
        TypedListLiteralNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptArrayNode(
            node.elements().stream()
                .map(element -> compileExpression(element, context))
                .toList()
        );
    }

    private static TypeScriptExpressionNode compileLocalReference(TypedLocalReferenceNode node) {
        return new TypeScriptReferenceNode(node.name());
    }

    private static TypeScriptExpressionNode compileLogicalAnd(TypedLogicalAndNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptLogicalAndNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static TypeScriptExpressionNode compileLogicalNot(TypedLogicalNotNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptLogicalNotNode(compileExpression(node.operand(), context));
    }

    private static TypeScriptExpressionNode compileLogicalOr(TypedLogicalOrNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptLogicalOrNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static TypeScriptExpressionNode compileMapLiteral(
        TypedMapLiteralNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptCallNewNode(
            new TypeScriptReferenceNode("Map"),
            List.of(),
            List.of(
                new TypeScriptArrayNode(
                    node.entries().stream()
                        .<TypeScriptExpressionNode>map(entry -> new TypeScriptArrayNode(List.of(
                            compileExpression(entry.key(), context),
                            compileExpression(entry.value(), context)
                        )))
                        .toList()
                )
            )
        );
    }

    private static TypeScriptExpressionNode compileMemberAccess(
        TypedMemberAccessNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptPropertyAccessNode(
            compileExpression(node.receiver(), context),
            node.memberName()
        );
    }

    private static TypeScriptExpressionNode compileMemberReference(
        TypedMemberReferenceNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptPropertyAccessNode(
            new TypeScriptReferenceNode("this"),
            node.name()
        );
    }

    public static TypeScriptModuleNode compileNamespace(TypedNamespaceNode node, SubtypeRelations subtypeRelations) {
        var name = String.join("/", node.name().parts());
        var context = new TypeScriptCodeGeneratorContext(subtypeRelations);

        var statements = new ArrayList<TypeScriptStatementNode>();

        node.imports().stream()
            .map(import_ -> compileImport(import_, node.name()))
            .forEachOrdered(statements::addAll);

        node.statements().stream()
            .map(statement -> compileNamespaceStatement(statement, context))
            .forEachOrdered(statements::add);

        statements.addAll(0, context.imports());

        var exports = node.type().fields().keySet().stream().sorted().toList();
        if (exports.size() > 0) {
            statements.add(new TypeScriptExportNode(exports));
        }

        return new TypeScriptModuleNode(name, statements);
    }

    public static TypeScriptStatementNode compileNamespaceStatement(
        TypedNamespaceStatementNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return node.accept(new TypedNamespaceStatementNode.Visitor<TypeScriptStatementNode>() {
            @Override
            public TypeScriptStatementNode visit(TypedBlankLineNode node) {
                return compileBlankLine(node, context);
            }

            @Override
            public TypeScriptStatementNode visit(TypedEnumNode node) {
                return compileEnum(node, context);
            }

            @Override
            public TypeScriptStatementNode visit(TypedFunctionNode node) {
                return compileFunction(node, context);
            }

            @Override
            public TypeScriptStatementNode visit(TypedInterfaceNode node) {
                return compileInterface(node, context);
            }

            @Override
            public TypeScriptStatementNode visit(TypedRecordNode node) {
                return compileRecord(node, context);
            }

            @Override
            public TypeScriptStatementNode visit(TypedSingleLineCommentNode node) {
                return compileSingleLineComment(node);
            }

            @Override
            public TypeScriptStatementNode visit(TypedTestNode node) {
                return compileTest(node, context);
            }

            @Override
            public TypeScriptStatementNode visit(TypedTestSuiteNode node) {
                return compileTestSuite(node, context);
            }
        });
    }

    private static TypeScriptParamNode compileParam(TypedParamNode node) {
        return new TypeScriptParamNode(node.name(), compileTypeLevelExpression(node.type()));
    }

    private static TypeScriptClassBodyDeclarationNode compileProperty(
        TypedPropertyNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptGetterNode(
            node.name(),
            compileTypeLevelExpression(node.type()),
            compileFunctionStatements(node.body(), context)
        );
    }

    private static TypeScriptStatementNode compileRecord(
        TypedRecordNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        var fields = new ArrayList<TypeScriptClassFieldNode>();

        var supertypes = node.supertypes();
        if (supertypes.stream().anyMatch(supertype -> Types.isSealedInterfaceType((Type) supertype.value()))) {
            fields.add(new TypeScriptClassFieldNode(
                "type",
                new TypeScriptStringLiteralNode(node.name()),
                Optional.of(new TypeScriptStringLiteralNode(node.name()))
            ));
        }

        node.fields().stream()
            .map(field -> new TypeScriptClassFieldNode(field.name(), compileTypeLevelExpression(field.type()), Optional.empty()))
            .collect(Collectors.toCollection(() -> fields));
        
        var body = node.body().stream()
            .map(declaration -> compileRecordBodyDeclaration(declaration, context))
            .toList();

        return new TypeScriptClassDeclarationNode(node.name(), fields, body);
    }

    private static TypeScriptClassBodyDeclarationNode compileRecordBodyDeclaration(
        TypedRecordBodyDeclarationNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return node.accept(new TypedRecordBodyDeclarationNode.Visitor<TypeScriptClassBodyDeclarationNode>() {
            @Override
            public TypeScriptClassBodyDeclarationNode visit(TypedBlankLineNode node) {
                return compileBlankLine(node, context);
            }

            @Override
            public TypeScriptClassBodyDeclarationNode visit(TypedFunctionNode node) {
                return compileFunction(node, context);
            }

            @Override
            public TypeScriptClassBodyDeclarationNode visit(TypedPropertyNode node) {
                return compileProperty(node, context);
            }

            @Override
            public TypeScriptClassBodyDeclarationNode visit(TypedSingleLineCommentNode node) {
                return compileSingleLineComment(node);
            }
        });
    }

    private static TypeScriptStatementNode compileReturn(TypedReturnNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptReturnNode(compileExpression(node.expression(), context));
    }

    private static TypeScriptSingleLineCommentNode compileSingleLineComment(TypedSingleLineCommentNode node) {
        return new TypeScriptSingleLineCommentNode(node.value());
    }

    private static TypeScriptExpressionNode compileStringEquals(TypedStringEqualsNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptStrictEqualsNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static TypeScriptExpressionNode compileStringLiteral(TypedStringLiteralNode node) {
        return new TypeScriptStringLiteralNode(node.value());
    }

    private static TypeScriptExpressionNode compileStringNotEqual(
        TypedStringNotEqualNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptStrictNotEqualNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static TypeScriptStatementNode compileSwitch(
        TypedSwitchNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        var switchExpression = compileExpression(node.expression(), context);

        return new TypeScriptSwitchNode(
            new TypeScriptPropertyAccessNode(switchExpression, "type"),
            node.cases().stream()
                .map(typedCaseNode -> {
                    var caseType = (RecordType) typedCaseNode.type().value();

                    var body = new ArrayList<TypeScriptStatementNode>();

                    body.addAll(compileFunctionStatements(typedCaseNode.body(), context));

                    return new TypeScriptSwitchCaseNode(
                        new TypeScriptStringLiteralNode(caseType.name()),
                        body
                    );
                })
                .toList()
        );
    }

    private static TypeScriptStatementNode compileTest(TypedTestNode node, TypeScriptCodeGeneratorContext context) {
        return TypeScript.expressionStatement(TypeScript.call(
            TypeScript.reference("test"),
            List.of(
                TypeScript.string(node.name()),
                TypeScriptFunctionExpressionNode.builder()
                    .addBodyStatements(compileFunctionStatements(node.body(), context))
                    .build()
            )
        ));
    }

    private static TypeScriptStatementNode compileTestSuite(
        TypedTestSuiteNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptExpressionStatementNode(new TypeScriptCallNode(
            new TypeScriptReferenceNode("suite"),
            List.of(
                new TypeScriptStringLiteralNode(node.name()),
                new TypeScriptFunctionExpressionNode(
                    List.of(),
                    node.body().stream()
                        .map(statement -> compileNamespaceStatement(statement, context))
                        .toList()
                )
            )
        ));
    }

    public static TypeScriptExpressionNode compileTypeLevelExpression(TypedTypeLevelExpressionNode node) {
        return node.accept(new TypedTypeLevelExpressionNode.Visitor<TypeScriptExpressionNode>() {
            @Override
            public TypeScriptExpressionNode visit(TypedConstructedTypeNode node) {
                if (node.receiver().value() == Types.OPTION_CONSTRUCTOR) {
                    return new TypeScriptUnionNode(List.of(
                        compileTypeLevelExpression(node.args().get(0).type()),
                        new TypeScriptReferenceNode("null")
                    ));
                } else {
                    return new TypeScriptConstructedTypeNode(
                        compileTypeLevelExpression(node.receiver()),
                        node.args().stream()
                            .map(arg -> compileTypeLevelExpression(arg.type()))
                            .toList()
                    );
                }
            }

            @Override
            public TypeScriptExpressionNode visit(TypedTypeLevelReferenceNode node) {
                var value = node.value();
                var builtinReference = builtinReference(value);
                if (builtinReference.isPresent()) {
                    return builtinReference.get();
                } else {
                    return new TypeScriptReferenceNode(node.name());
                }
            }
        });
    }

    private static Optional<TypeScriptExpressionNode> builtinReference(TypeLevelValue value) {
        if (value == BoolType.INSTANCE) {
            return Optional.of(new TypeScriptReferenceNode("boolean"));
        } else if (value == IntType.INSTANCE) {
            return Optional.of(new TypeScriptReferenceNode("number"));
        } else if (value == StringType.INSTANCE) {
            return Optional.of(new TypeScriptReferenceNode("string"));
        } else if (value instanceof Type type) {
            return TypeScriptMacros.compileTypeReference(type);
        } else if (value instanceof TypeConstructor typeConstructor) {
            return TypeScriptMacros.compileTypeConstructorReference(typeConstructor);
        } else {
            return Optional.empty();
        }
    }

    private static TypeScriptStatementNode compileVar(TypedVarNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptLetNode(node.name(), compileExpression(node.expression(), context));
    }
}
