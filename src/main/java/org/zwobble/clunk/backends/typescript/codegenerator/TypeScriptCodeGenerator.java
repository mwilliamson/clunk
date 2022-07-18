package org.zwobble.clunk.backends.typescript.codegenerator;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.typescript.ast.*;
import org.zwobble.clunk.typechecker.SubtypeLookup;
import org.zwobble.clunk.types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TypeScriptCodeGenerator {
    private interface TypeScriptMacro {
        TypeScriptExpressionNode compileReceiver(TypeScriptCodeGeneratorContext context);
    }

    private static final Map<NamespaceName, Map<String, TypeScriptMacro>> MACROS = Map.ofEntries(
        Map.entry(
            NamespaceName.fromParts("stdlib", "assertions"),
            Map.ofEntries(
                Map.entry("assertThat", new TypeScriptMacro() {
                    @Override
                    public TypeScriptExpressionNode compileReceiver(TypeScriptCodeGeneratorContext context) {
                        context.addImport("@mwilliamson/precisely", "assertThat");
                        return new TypeScriptReferenceNode("assertThat");
                    }
                })
            )
        ),
        Map.entry(
            NamespaceName.fromParts("stdlib", "matchers"),
            Map.ofEntries(
                Map.entry("equalTo", new TypeScriptMacro() {
                    @Override
                    public TypeScriptExpressionNode compileReceiver(TypeScriptCodeGeneratorContext context) {
                        context.addImport("@mwilliamson/precisely", "equalTo");
                        return new TypeScriptReferenceNode("equalTo");
                    }
                })
            )
        )
    );

    private static Optional<TypeScriptMacro> lookupMacro(Type type) {
        if (type instanceof StaticFunctionType staticFunctionType) {
            var macro = MACROS.getOrDefault(staticFunctionType.namespaceName(), Map.of())
                .get(staticFunctionType.functionName());
            return Optional.ofNullable(macro);
        } else {
            return Optional.empty();
        }
    }

    private static TypeScriptStatementNode compileBlankLine(TypedBlankLineNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptBlankLineNode();
    }

    private static TypeScriptExpressionNode compileBoolLiteral(TypedBoolLiteralNode node) {
        return new TypeScriptBoolLiteralNode(node.value());
    }

    private static TypeScriptExpressionNode compileCall(TypedCallNode node, TypeScriptCodeGeneratorContext context) {
        var typeScriptReceiver = compileCallReceiver(node.receiver(), context);
        var typeScriptArgs = node.positionalArgs().stream()
            .map(arg -> compileExpression(arg, context))
            .toList();

        if (node.receiver().type() instanceof TypeLevelValueType typeLevelValueType) {
            return new TypeScriptCallNewNode(typeScriptReceiver, typeScriptArgs);
        } else {
            return new TypeScriptCallNode(
                typeScriptReceiver,
                typeScriptArgs
            );
        }
    }

    private static TypeScriptExpressionNode compileCallReceiver(TypedExpressionNode receiver, TypeScriptCodeGeneratorContext context) {
        var macro = lookupMacro(receiver.type());

        if (macro.isPresent()) {
            return macro.get().compileReceiver(context);
        } else {
            return compileExpression(receiver, context);
        }
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
            public TypeScriptExpressionNode visit(TypedCallNode node) {
                return compileCall(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedIntLiteralNode node) {
                return compileIntLiteral(node);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedMemberAccessNode node) {
                return compileMemberAccess(node, context);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedReferenceNode node) {
                return compileReference(node);
            }

            @Override
            public TypeScriptExpressionNode visit(TypedStringLiteralNode node) {
                return compileStringLiteral(node);
            }
        });
    }

    private static TypeScriptStatementNode compileExpressionStatement(
        TypedExpressionStatementNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptExpressionStatementNode(compileExpression(node.expression(), context));
    }

    private static TypeScriptStatementNode compileFunction(TypedFunctionNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptFunctionDeclarationNode(
            node.name(),
            node.params().stream().map(param -> compileParam(param)).toList(),
            compileTypeLevelExpression(node.returnType()),
            node.body().stream().map(statement -> compileFunctionStatement(statement, context)).toList()
        );
    }

    public static TypeScriptStatementNode compileFunctionStatement(
        TypedFunctionStatementNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return node.accept(new TypedFunctionStatementNode.Visitor<TypeScriptStatementNode>() {
            @Override
            public TypeScriptStatementNode visit(TypedBlankLineNode node) {
                return compileBlankLine(node, context);
            }

            @Override
            public TypeScriptStatementNode visit(TypedExpressionStatementNode node) {
                return compileExpressionStatement(node, context);
            }

            @Override
            public TypeScriptStatementNode visit(TypedIfStatementNode node) {
                return compileIfStatement(node, context);
            }

            @Override
            public TypeScriptStatementNode visit(TypedReturnNode node) {
                return compileReturn(node, context);
            }

            @Override
            public TypeScriptStatementNode visit(TypedVarNode node) {
                return compileVar(node, context);
            }
        });
    }

    private static TypeScriptStatementNode compileIfStatement(
        TypedIfStatementNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptIfStatementNode(
            node.conditionalBranches().stream()
                .map(conditionalBranch -> new TypeScriptConditionalBranchNode(
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

    private static TypeScriptStatementNode compileInterface(
        TypedInterfaceNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return new TypeScriptTypeDeclarationNode(
            node.name(),
            new TypeScriptUnionNode(
                context.subtypesOf(node.type()).stream()
                    .map(subtype -> new TypeScriptReferenceNode(subtype.name()))
                    .toList()
            )
        );
    }

    private static TypeScriptExpressionNode compileIntLiteral(TypedIntLiteralNode node) {
        return new TypeScriptNumberLiteralNode(node.value());
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

    public static TypeScriptModuleNode compileNamespace(TypedNamespaceNode node, SubtypeLookup subtypeLookup) {
        var name = String.join("/", node.name().parts());
        var context = new TypeScriptCodeGeneratorContext(subtypeLookup);

        var statements = new ArrayList<TypeScriptStatementNode>();

        node.statements().stream()
            .map(statement -> compileNamespaceStatement(statement, context))
            .forEachOrdered(statements::add);

        statements.addAll(0, context.imports());

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
            public TypeScriptStatementNode visit(TypedTestNode node) {
                return compileTest(node, context);
            }
        });
    }

    private static TypeScriptParamNode compileParam(TypedParamNode node) {
        return new TypeScriptParamNode(node.name(), compileTypeLevelExpression(node.type()));
    }

    private static TypeScriptStatementNode compileRecord(
        TypedRecordNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        var fields = new ArrayList<TypeScriptClassFieldNode>();

        var supertypes = node.supertypes();
        if (!supertypes.isEmpty()) {
            fields.add(new TypeScriptClassFieldNode(
                "type",
                new TypeScriptStringLiteralNode(node.name()),
                Optional.of(new TypeScriptStringLiteralNode(node.name()))
            ));
        }

        node.fields().stream()
            .map(field -> new TypeScriptClassFieldNode(field.name(), compileTypeLevelExpression(field.type()), Optional.empty()))
            .collect(Collectors.toCollection(() -> fields));

        return new TypeScriptClassDeclarationNode(node.name(), fields);
    }

    private static TypeScriptExpressionNode compileReference(TypedReferenceNode node) {
        return new TypeScriptReferenceNode(node.name());
    }

    private static TypeScriptStatementNode compileReturn(TypedReturnNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptReturnNode(compileExpression(node.expression(), context));
    }

    private static TypeScriptExpressionNode compileStringLiteral(TypedStringLiteralNode node) {
        return new TypeScriptStringLiteralNode(node.value());
    }

    private static TypeScriptStatementNode compileTest(TypedTestNode node, TypeScriptCodeGeneratorContext context) {
        return TypeScript.expressionStatement(TypeScript.call(
            TypeScript.reference("test"),
            List.of(
                TypeScript.string(node.name()),
                TypeScriptFunctionExpressionNode.builder()
                    .addBodyStatements(node.body().stream().map(statement -> compileFunctionStatement(statement, context)).toList())
                    .build()
            )
        ));
    }

    public static TypeScriptExpressionNode compileTypeLevelExpression(TypedTypeLevelExpressionNode node) {
        return node.accept(new TypedTypeLevelExpressionNode.Visitor<TypeScriptExpressionNode>() {
            @Override
            public TypeScriptExpressionNode visit(TypedConstructedTypeNode node) {
                if (node.receiver().value() == OptionTypeConstructor.INSTANCE) {
                    return new TypeScriptUnionNode(List.of(
                        compileTypeLevelExpression(node.args().get(0)),
                        new TypeScriptReferenceNode("null")
                    ));
                } else {
                    return new TypeScriptConstructedTypeNode(
                        compileTypeLevelExpression(node.receiver()),
                        node.args().stream()
                            .map(arg -> compileTypeLevelExpression(arg))
                            .toList()
                    );
                }
            }

            @Override
            public TypeScriptExpressionNode visit(TypedTypeLevelReferenceNode node) {
                var value = node.value();
                if (value == BoolType.INSTANCE) {
                    return new TypeScriptReferenceNode("boolean");
                } else if (value == IntType.INSTANCE) {
                    return new TypeScriptReferenceNode("number");
                } else if (value == ListTypeConstructor.INSTANCE) {
                    return new TypeScriptReferenceNode("Array");
                } else if (value == StringType.INSTANCE) {
                    return new TypeScriptReferenceNode("string");
                } else {
                    return new TypeScriptReferenceNode(node.name());
                }
            }
        });
    }

    private static TypeScriptStatementNode compileVar(TypedVarNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptLetNode(node.name(), compileExpression(node.expression(), context));
    }
}
