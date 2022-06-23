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

    private static TypeScriptExpressionNode compileBoolLiteral(TypedBoolLiteralNode node) {
        return new TypeScriptBoolLiteralNode(node.value());
    }

    private static TypeScriptExpressionNode compileCall(TypedCallNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptCallNode(
            compileCallReceiver(node.receiver(), context),
            node.positionalArgs().stream().map(arg -> compileExpression(arg, context)).toList()
        );
    }

    private static TypeScriptExpressionNode compileCallReceiver(TypedExpressionNode receiver, TypeScriptCodeGeneratorContext context) {
        var macro = lookupMacro(receiver.type());

        if (macro.isPresent()) {
            return macro.get().compileReceiver(context);
        } else {
            return compileExpression(receiver, context);
        }
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
            compileStaticExpression(node.returnType()),
            node.body().stream().map(statement -> compileFunctionStatement(statement, context)).toList()
        );
    }

    public static TypeScriptStatementNode compileFunctionStatement(
        TypedFunctionStatementNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        return node.accept(new TypedFunctionStatementNode.Visitor<TypeScriptStatementNode>() {
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
        return new TypeScriptParamNode(node.name(), compileStaticExpression(node.type()));
    }

    private static TypeScriptInterfaceDeclarationNode compileRecord(
        TypedRecordNode node,
        TypeScriptCodeGeneratorContext context
    ) {
        var fields = new ArrayList<TypeScriptInterfaceFieldNode>();

        var supertypes = context.supertypesOf(node.type());
        if (!supertypes.isEmpty()) {
            fields.add(new TypeScriptInterfaceFieldNode("type", new TypeScriptStringLiteralNode(node.name())));
        }

        node.fields().stream()
            .map(field -> new TypeScriptInterfaceFieldNode(field.name(), compileStaticExpression(field.type())))
            .collect(Collectors.toCollection(() -> fields));

        return new TypeScriptInterfaceDeclarationNode(node.name(), fields);
    }

    private static TypeScriptExpressionNode compileReference(TypedReferenceNode node) {
        return new TypeScriptReferenceNode(node.name());
    }

    private static TypeScriptStatementNode compileReturn(TypedReturnNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptReturnNode(compileExpression(node.expression(), context));
    }

    public static TypeScriptReferenceNode compileStaticExpression(TypedStaticExpressionNode node) {
        return new TypeScriptReferenceNode(compileType(node.type()));
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

    private static TypeScriptStatementNode compileVar(TypedVarNode node, TypeScriptCodeGeneratorContext context) {
        return new TypeScriptLetNode(node.name(), compileExpression(node.expression(), context));
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
