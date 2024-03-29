package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.ast.SourceType;
import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.python.ast.*;
import org.zwobble.clunk.types.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.zwobble.clunk.backends.CaseConverter.camelCaseToSnakeCase;

public class PythonCodeGenerator {
    static PythonArgsNode compileArgs(
        TypedArgsNode args,
        PythonCodeGeneratorContext context
    ) {
        var positional = args.positional().stream()
            .map(arg -> compileExpression(arg, context))
            .toList();

        var keyword = args.named().stream()
            .map(arg -> new PythonKeywordArgumentNode(
                pythonizeName(arg.name()),
                compileExpression(arg.expression(), context)
            ))
            .toList();

        return new PythonArgsNode(positional, keyword);
    }

    private static PythonStatementNode compileBlankLine(TypedBlankLineNode node, PythonCodeGeneratorContext context) {
        return new PythonBlankLineNode();
    }

    private static PythonExpressionNode compileBoolLiteral(TypedBoolLiteralNode node) {
        return new PythonBoolLiteralNode(node.value());
    }

    private static PythonExpressionNode compileCallConstructor(TypedCallConstructorNode node, PythonCodeGeneratorContext context) {
        var classMacro = PythonMacros.lookupClassMacro(node.type());
        if (classMacro.isPresent()) {
            var pythonArgs = compileArgs(node.args(), context);
            return classMacro.get().compileConstructorCall(pythonArgs);
        } else {
            return new PythonCallNode(
                compileExpression(node.receiver(), context),
                compileArgs(node.args(), context)
            );
        }
    }

    private static PythonExpressionNode compileCallMethod(TypedCallMethodNode node, PythonCodeGeneratorContext context) {
        var receiver = node.receiver();
        if (receiver.isPresent()) {
            var classMacro = PythonMacros.lookupClassMacro(receiver.get().type());
            if (classMacro.isPresent()) {
                var pythonReceiver = compileExpression(receiver.get(), context);
                return classMacro.get().compileMethodCall(
                    pythonReceiver,
                    node.methodName(),
                    compileArgs(node.args(), context)
                );
            }
        }

        return new PythonCallNode(
            new PythonAttrAccessNode(
                receiver.map(r -> compileExpression(r, context)).orElse(new PythonReferenceNode("self")),
                pythonizeName(node.methodName())
            ),
            compileArgs(node.args(), context)
        );
    }

    private static PythonExpressionNode compileCallStaticFunction(
        TypedCallStaticFunctionNode node,
        PythonCodeGeneratorContext context
    ) {
        var macro = PythonMacros.lookupStaticFunctionMacro(node.receiver().type());

        if (macro.isPresent()) {
            return macro.get().compileCall(
                compileArgs(node.args(), context),
                context
            );
        } else {
            return new PythonCallNode(
                compileExpression(node.receiver(), context),
                compileArgs(node.args(), context)
            );
        }
    }

    private static PythonExpressionNode compileCastUnsafe(
        TypedCastUnsafeNode node,
        PythonCodeGeneratorContext context
    ) {
        return compileExpression(node.expression(), context);
    }

    private static PythonStatementNode compileEnum(
        TypedEnumNode node,
        PythonCodeGeneratorContext context
    ) {
        context.addImport(List.of("enum"));

        return new PythonClassDeclarationNode(
            node.type().name(),
            List.of(),
            List.of(new PythonAttrAccessNode(new PythonReferenceNode("enum"), "Enum")),
            node.type().members().stream()
                .map(member -> new PythonAssignmentNode(
                    member,
                    Optional.empty(),
                    Optional.of(new PythonCallNode(
                        new PythonAttrAccessNode(new PythonReferenceNode("enum"), "auto"),
                        new PythonArgsNode(
                            List.of(),
                            List.of()
                        )
                    ))
                ))
                .toList()
        );
    }

    public static PythonExpressionNode compileExpression(
        TypedExpressionNode node,
        PythonCodeGeneratorContext context
    ) {
        return node.accept(new TypedExpressionNode.Visitor<PythonExpressionNode>() {
            @Override
            public PythonExpressionNode visit(TypedBoolLiteralNode node) {
                return compileBoolLiteral(node);
            }

            @Override
            public PythonExpressionNode visit(TypedCallConstructorNode node) {
                return compileCallConstructor(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedCallMethodNode node) {
                return compileCallMethod(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedCallStaticFunctionNode node) {
                return compileCallStaticFunction(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedCastUnsafeNode node) {
                return compileCastUnsafe(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedIndexNode node) {
                return compileIndex(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedInstanceOfNode node) {
                return compileInstanceOf(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedIntAddNode node) {
                return compileIntAdd(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedIntEqualsNode node) {
                return compileIntEquals(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedIntLiteralNode node) {
                return compileIntLiteral(node);
            }

            @Override
            public PythonExpressionNode visit(TypedIntNotEqualNode node) {
                return compileIntNotEqual(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedListComprehensionNode node) {
                return compileListComprehension(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedListLiteralNode node) {
                return compileListLiteral(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedLocalReferenceNode node) {
                return compileLocalReference(node);
            }

            @Override
            public PythonExpressionNode visit(TypedLogicalAndNode node) {
                return compileLogicalAnd(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedLogicalNotNode node) {
                return compileLogicalNot(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedLogicalOrNode node) {
                return compileLogicalOr(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedMapLiteralNode node) {
                return compileMapLiteral(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedMemberAccessNode node) {
                return compileMemberAccess(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedMemberDefinitionReferenceNode node) {
                return compileMemberDefinitionReference(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedMemberReferenceNode node) {
                return compileMemberReference(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedStaticMethodToFunctionNode node) {
                return compileExpression(node.method(), context);
            }

            @Override
            public PythonExpressionNode visit(TypedStringEqualsNode node) {
                return compileStringEquals(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedStringLiteralNode node) {
                return compileStringLiteral(node);
            }

            @Override
            public PythonExpressionNode visit(TypedStringNotEqualNode node) {
                return compileStringNotEqual(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedStructuredEqualsNode node) {
                return compileStructuredEquals(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedStructuredNotEqualNode node) {
                return compileStructuredNotEqual(node, context);
            }
        });
    }

    private static PythonStatementNode compileExpressionStatement(
        TypedExpressionStatementNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonExpressionStatementNode(compileExpression(node.expression(), context));
    }

    private static PythonStatementNode compileForEach(
        TypedForEachNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonForEachNode(
            pythonizeName(node.targetName()),
            compileExpression(node.iterable(), context),
            compileFunctionStatements(node.body(), context)
        );
    }

    private static PythonStatementNode compileFunction(TypedFunctionNode node, PythonCodeGeneratorContext context) {
        return compileFunction(node, false, context);
    }

    private static PythonFunctionNode compileFunction(TypedFunctionNode node, boolean hasSelf, PythonCodeGeneratorContext context) {
        return new PythonFunctionNode(
            pythonizeName(node.name()),
            List.of(),
            compileParams(node.params(), hasSelf),
            compileFunctionStatements(node.body(), context)
        );
    }

    private static List<PythonStatementNode> compileFunctionStatements(List<TypedFunctionStatementNode> node, PythonCodeGeneratorContext context) {
        return node.stream().flatMap(statement -> compileFunctionStatement(statement, context).stream()).toList();
    }

    public static List<PythonStatementNode> compileFunctionStatement(TypedFunctionStatementNode node, PythonCodeGeneratorContext context) {
        return node.accept(new TypedFunctionStatementNode.Visitor<>() {
            @Override
            public List<PythonStatementNode> visit(TypedBlankLineNode node) {
                return List.of(compileBlankLine(node, context));
            }

            @Override
            public List<PythonStatementNode> visit(TypedExpressionStatementNode node) {
                return List.of(compileExpressionStatement(node, context));
            }

            @Override
            public List<PythonStatementNode> visit(TypedForEachNode node) {
                return List.of(compileForEach(node, context));
            }

            @Override
            public List<PythonStatementNode> visit(TypedIfStatementNode node) {
                return List.of(compileIfStatement(node, context));
            }

            @Override
            public List<PythonStatementNode> visit(TypedReturnNode node) {
                return List.of(compileReturn(node, context));
            }

            @Override
            public List<PythonStatementNode> visit(TypedSingleLineCommentNode node) {
                return List.of(compileSingleLineComment(node));
            }

            @Override
            public List<PythonStatementNode> visit(TypedSwitchNode node) {
                return compileSwitch(node, context);
            }

            @Override
            public List<PythonStatementNode> visit(TypedTypeNarrowNode node) {
                return List.of();
            }

            @Override
            public List<PythonStatementNode> visit(TypedVarNode node) {
                return List.of(compileVar(node, context));
            }
        });
    }

    private static PythonStatementNode compileIfStatement(
        TypedIfStatementNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonIfStatementNode(
            node.conditionalBranches().stream()
                .map(conditionalBranch -> new PythonConditionalBranchNode(
                    compileExpression(conditionalBranch.condition(), context),
                    compileFunctionStatements(conditionalBranch.body(), context)
                ))
                .toList(),
            compileFunctionStatements(node.elseBody(), context)
        );
    }

    private static List<PythonStatementNode> compileImport(TypedImportNode import_) {
        var macro = PythonMacros.lookupStaticFunctionMacro(import_.type());
        if (macro.isPresent()) {
            return List.of();
        } else if (import_.fieldName().isPresent()) {
            return List.of(new PythonImportFromNode(
                namespaceIdToModuleName(import_.namespaceId()),
                List.of(pythonizeName(import_.fieldName().get()))
            ));
        } else if (import_.namespaceName().parts().size() == 1) {
            return List.of(new PythonImportNode(
                namespaceIdToModuleName(import_.namespaceId())
            ));
        } else {
            var moduleName = namespaceIdToModuleName(import_.namespaceId());

            return List.of(new PythonImportFromNode(
                moduleName.subList(0, moduleName.size() - 1),
                List.of(moduleName.get(moduleName.size() - 1))
            ));
        }
    }

    private static PythonExpressionNode compileIndex(
        TypedIndexNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonSubscriptionNode(
            compileExpression(node.receiver(), context),
            List.of(compileExpression(node.index(), context))
        );
    }

    private static PythonExpressionNode compileInstanceOf(
        TypedInstanceOfNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonCallNode(
            new PythonReferenceNode("isinstance"),
            new PythonArgsNode(
                List.of(
                    compileExpression(node.expression(), context),
                    compileTypeLevelExpression(node.typeExpression(), context)
                ),
                List.of()
            )
        );
    }

    private static PythonStatementNode compileInterface(TypedInterfaceNode node, PythonCodeGeneratorContext context) {
        return new PythonClassDeclarationNode(node.name(), List.of(), List.of(), List.of());
    }

    private static PythonExpressionNode compileIntAdd(TypedIntAddNode node, PythonCodeGeneratorContext context) {
        return new PythonAddNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static PythonExpressionNode compileIntEquals(TypedIntEqualsNode node, PythonCodeGeneratorContext context) {
        return new PythonEqualsNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static PythonExpressionNode compileIntLiteral(TypedIntLiteralNode node) {
        return new PythonIntLiteralNode(BigInteger.valueOf(node.value()));
    }

    private static PythonExpressionNode compileIntNotEqual(
        TypedIntNotEqualNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonNotEqualNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static PythonExpressionNode compileListComprehension(
        TypedListComprehensionNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonListComprehensionNode(
            compileExpression(node.yield(), context),
            node.forClauses().stream()
                .map(forClause -> new PythonComprehensionForClauseNode(
                    forClause.targetName(),
                    compileExpression(forClause.iterable(), context),
                    forClause.ifClauses().stream()
                        .map(ifClause -> compileExpression(ifClause.condition(), context))
                        .toList()
                ))
                .toList()
        );
    }

    private static PythonExpressionNode compileListLiteral(
        TypedListLiteralNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonListNode(
            node.elements().stream()
                .map(element -> compileExpression(element, context))
                .toList()
        );
    }

    private static PythonExpressionNode compileLocalReference(TypedLocalReferenceNode node) {
        return new PythonReferenceNode(pythonizeName(node.name()));
    }

    private static PythonExpressionNode compileLogicalAnd(TypedLogicalAndNode node, PythonCodeGeneratorContext context) {
        return new PythonBoolAndNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static PythonExpressionNode compileLogicalNot(TypedLogicalNotNode node, PythonCodeGeneratorContext context) {
        return new PythonBoolNotNode(compileExpression(node.operand(), context));
    }

    private static PythonExpressionNode compileLogicalOr(TypedLogicalOrNode node, PythonCodeGeneratorContext context) {
        return new PythonBoolOrNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static PythonExpressionNode compileMapLiteral(
        TypedMapLiteralNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonDictNode(
            node.entries().stream()
                .map(entry -> new PythonDictItemNode(
                    compileExpression(entry.key(), context),
                    compileExpression(entry.value(), context)
                ))
                .toList()
        );
    }

    private static PythonExpressionNode compileMemberAccess(
        TypedMemberAccessNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonAttrAccessNode(
            compileExpression(node.receiver(), context),
            pythonizeName(node.memberName())
        );
    }

    private static PythonExpressionNode compileMemberDefinitionReference(
        TypedMemberDefinitionReferenceNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonStringLiteralNode(node.memberName());
    }

    private static PythonExpressionNode compileMemberReference(
        TypedMemberReferenceNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonAttrAccessNode(
            new PythonReferenceNode("self"),
            pythonizeName(node.name())
        );
    }

    private static PythonStatementNode compileMethod(TypedFunctionNode node, PythonCodeGeneratorContext context) {
        return compileFunction(node, true, context);
    }

    public static PythonModuleNode compileNamespace(TypedNamespaceNode node) {
        var context = PythonCodeGeneratorContext.initial();
        var moduleName = namespaceIdToModuleName(node.id());

        var statements = new ArrayList<PythonStatementNode>();

        node.imports().stream()
            .map(import_ -> compileImport(import_))
            .forEachOrdered(statements::addAll);

        node.statements().stream()
            .map(statement -> compileNamespaceStatement(statement, context))
            .forEachOrdered(statements::add);

        statements.addAll(0, context.imports());

        return new PythonModuleNode(moduleName, statements);
    }

    public static PythonStatementNode compileNamespaceStatement(
        TypedNamespaceStatementNode node,
        PythonCodeGeneratorContext context
    ) {
        return node.accept(new TypedNamespaceStatementNode.Visitor<PythonStatementNode>() {
            @Override
            public PythonStatementNode visit(TypedBlankLineNode node) {
                return compileBlankLine(node, context);
            }

            @Override
            public PythonStatementNode visit(TypedEnumNode node) {
                return compileEnum(node, context);
            }

            @Override
            public PythonStatementNode visit(TypedFunctionNode node) {
                return compileFunction(node, context);
            }

            @Override
            public PythonStatementNode visit(TypedInterfaceNode node) {
                return compileInterface(node, context);
            }

            @Override
            public PythonStatementNode visit(TypedRecordNode node) {
                return compileRecord(node, context);
            }

            @Override
            public PythonStatementNode visit(TypedSingleLineCommentNode node) {
                return compileSingleLineComment(node);
            }

            @Override
            public PythonStatementNode visit(TypedTestNode node) {
                return compileTest(node, context);
            }

            @Override
            public PythonStatementNode visit(TypedTestSuiteNode node) {
                return compileTestSuite(node, context);
            }
        });
    }

    private static String compileParam(TypedParamNode node) {
        return pythonizeName(node.name());
    }

    private static PythonParamsNode compileParams(TypedParamsNode node, boolean hasSelf) {
        var positional = node.positional().stream()
            .map(param -> compileParam(param))
            .toList();

        var keyword = node.named().stream()
            .map(param -> compileParam(param))
            .toList();

        return new PythonParamsNode(hasSelf, positional, keyword);
    }

    private static PythonStatementNode compileProperty(
        TypedPropertyNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonFunctionNode(
            pythonizeName(node.name()),
            List.of(Python.reference("property")),
            new PythonParamsNode(
                true,
                List.of(),
                List.of()
            ),
            compileFunctionStatements(node.body(), context)
        );
    }

    public static PythonClassDeclarationNode compileRecord(
        TypedRecordNode node,
        PythonCodeGeneratorContext context
    ) {
        context.addImport(List.of("dataclasses"));

        var decorators = List.of(
            Python.call(
                Python.attr(Python.reference("dataclasses"), "dataclass"),
                List.of(Python.kwarg("frozen", Python.TRUE))
            )
        );

        var body = new ArrayList<PythonStatementNode>();

        node.fields().stream()
            .map(field -> Python.variableType(
                pythonizeName(field.name()),
                compileTypeLevelExpression(field.type(), context)
            ))
            .collect(Collectors.toCollection(() -> body));
        
        for (var bodyDeclaration : node.body()) {
            body.add(compileRecordBodyDeclaration(bodyDeclaration, context));
        }

        if (node.supertypes().stream().anyMatch(supertypeNode -> {
            var typeLevelValue = supertypeNode.value();
            return typeLevelValue instanceof Type type && Types.isSealedInterfaceType(type);
        })) {
            body.add(generateAcceptMethod(node));
        }

        return new PythonClassDeclarationNode(node.name(), decorators, List.of(), body);
    }

    private static PythonFunctionNode generateAcceptMethod(TypedRecordNode node) {
        return new PythonFunctionNode(
            "accept",
            List.of(),
            new PythonParamsNode(
                true,
                List.of("visitor"),
                List.of()
            ),
            List.of(
                new PythonReturnNode(
                    new PythonCallNode(
                        new PythonAttrAccessNode(
                            new PythonReferenceNode("visitor"),
                            generateVisitMethodName(node.type())
                        ),
                        new PythonArgsNode(
                            List.of(new PythonReferenceNode("self")),
                            List.of()
                        )
                    )
                )
            )
        );
    }

    private static String generateVisitMethodName(RecordType type) {
        return "visit_" + camelCaseToSnakeCase(type.name());
    }

    private static PythonStatementNode compileRecordBodyDeclaration(
        TypedRecordBodyDeclarationNode node,
        PythonCodeGeneratorContext context
    ) {
        return node.accept(new TypedRecordBodyDeclarationNode.Visitor<PythonStatementNode>() {
            @Override
            public PythonStatementNode visit(TypedBlankLineNode node) {
                return compileBlankLine(node, context);
            }

            @Override
            public PythonStatementNode visit(TypedFunctionNode node) {
                return compileMethod(node, context);
            }

            @Override
            public PythonStatementNode visit(TypedPropertyNode node) {
                return compileProperty(node, context);
            }

            @Override
            public PythonStatementNode visit(TypedSingleLineCommentNode node) {
                return compileSingleLineComment(node);
            }
        });
    }

    private static PythonStatementNode compileReturn(TypedReturnNode node, PythonCodeGeneratorContext context) {
        return new PythonReturnNode(compileExpression(node.expression(), context));
    }

    private static PythonStatementNode compileSingleLineComment(TypedSingleLineCommentNode node) {
        return new PythonSingleLineCommentNode(node.value());
    }

    private static PythonExpressionNode compileStringEquals(TypedStringEqualsNode node, PythonCodeGeneratorContext context) {
        return new PythonEqualsNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static PythonExpressionNode compileStringLiteral(TypedStringLiteralNode node) {
        return new PythonStringLiteralNode(node.value());
    }

    private static PythonExpressionNode compileStringNotEqual(TypedStringNotEqualNode node, PythonCodeGeneratorContext context) {
        return new PythonNotEqualNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static PythonExpressionNode compileStructuredEquals(
        TypedStructuredEqualsNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonEqualsNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static PythonExpressionNode compileStructuredNotEqual(
        TypedStructuredNotEqualNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonNotEqualNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static List<PythonStatementNode> compileSwitch(TypedSwitchNode node, PythonCodeGeneratorContext context) {
        var visitorDeclaration = new PythonClassDeclarationNode(
            "Visitor",
            List.of(),
            List.of(),
            node.cases().stream()
                .map(switchCase -> new PythonFunctionNode(
                    generateVisitMethodName((RecordType) switchCase.type().value()),
                    List.of(),
                    new PythonParamsNode(
                        true,
                        List.of(pythonizeName(node.expression().name())),
                        List.of()
                    ),
                    compileFunctionStatements(switchCase.body(), context)
                ))
                .toList()
        );

        var acceptCall = new PythonCallNode(
            new PythonAttrAccessNode(
                compileExpression(node.expression(), context),
                "accept"
            ),
            new PythonArgsNode(
                List.of(
                    new PythonCallNode(
                        new PythonReferenceNode("Visitor"),
                        new PythonArgsNode(
                            List.of(),
                            List.of()
                        )
                    )
                ),
                List.of()
            )
        );

        return List.of(
            visitorDeclaration,
            node.returns() ? new PythonReturnNode(acceptCall) : new PythonExpressionStatementNode(acceptCall)
        );
    }

    private static PythonStatementNode compileTest(TypedTestNode node, PythonCodeGeneratorContext context) {
        return new PythonFunctionNode(
            PythonTestNames.generateTestFunctionName(node.name()),
            List.of(),
            new PythonParamsNode(
                context.isInClass(),
                List.of(),
                List.of()
            ),
            compileFunctionStatements(node.body(), context)
        );
    }

    private static PythonStatementNode compileTestSuite(
        TypedTestSuiteNode node,
        PythonCodeGeneratorContext context
    ) {
        var className = PythonTestNames.generateTestClassName(node.name());

        var bodyContext = context.enterClass();

        return new PythonClassDeclarationNode(
            className,
            List.of(),
            List.of(),
            node.body().stream()
                .map(statement -> compileNamespaceStatement(statement, bodyContext))
                .toList()
        );
    }

    public static PythonExpressionNode compileTypeLevelExpression(
        TypedTypeLevelExpressionNode node,
        PythonCodeGeneratorContext context
    ) {
        return node.accept(new TypedTypeLevelExpressionNode.Visitor<PythonExpressionNode>() {
            @Override
            public PythonExpressionNode visit(TypedConstructedTypeNode node) {
                return new PythonSubscriptionNode(
                    compileTypeLevelExpression(node.receiver(), context),
                    node.args().stream()
                        .map(arg -> compileTypeLevelExpression(arg.type(), context))
                        .toList()
                );
            }

            @Override
            public PythonExpressionNode visit(TypedTypeLevelReferenceNode node) {
                var typeLevelValue = node.value();

                if (typeLevelValue == BoolType.INSTANCE) {
                    return new PythonReferenceNode("bool");
                } else if (typeLevelValue == IntType.INSTANCE) {
                    return new PythonReferenceNode("int");
                } else if (typeLevelValue == Types.LIST_CONSTRUCTOR) {
                    context.addImport(List.of("typing"));
                    return new PythonAttrAccessNode(
                        new PythonReferenceNode("typing"),
                        "List"
                    );
                } else if (typeLevelValue == Types.OPTION_CONSTRUCTOR) {
                    context.addImport(List.of("typing"));
                    return new PythonAttrAccessNode(
                        new PythonReferenceNode("typing"),
                        "Optional"
                    );
                } else if (typeLevelValue == StringType.INSTANCE) {
                    return new PythonReferenceNode("str");
                } else {
                    return new PythonReferenceNode(node.name());
                }
            }
        });
    }

    private static PythonStatementNode compileVar(TypedVarNode node, PythonCodeGeneratorContext context) {
        return new PythonAssignmentNode(
            pythonizeName(node.name()),
            Optional.empty(),
            Optional.of(compileExpression(node.expression(), context))
        );
    }

    private static List<String> namespaceIdToModuleName(NamespaceId id) {
        var moduleName = new ArrayList<>(id.name().parts());
        if (id.sourceType().equals(SourceType.TEST)) {
            moduleName.set(moduleName.size() - 1, moduleName.get(moduleName.size() - 1) + "_test");
        }
        return moduleName;
    }

    private static String pythonizeName(String name) {
        if (Character.isLowerCase(name.codePointAt(0))) {
            return camelCaseToSnakeCase(name);
        } else {
            return name;
        }
    }
}
