package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.python.ast.*;
import org.zwobble.clunk.types.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.zwobble.clunk.backends.CaseConverter.camelCaseToSnakeCase;

public class PythonCodeGenerator {
    private interface PythonMacro {
        PythonExpressionNode compileReceiver(PythonCodeGeneratorContext context);
    }

    private static final Map<NamespaceName, Map<String, PythonMacro>> STATIC_FUNCTION_MACROS = Map.ofEntries(
        Map.entry(
            NamespaceName.fromParts("stdlib", "assertions"),
            Map.ofEntries(
                Map.entry("assertThat", new PythonMacro() {
                    @Override
                    public PythonExpressionNode compileReceiver(PythonCodeGeneratorContext context) {
                        context.addImport(List.of("precisely", "assert_that"));
                        return new PythonReferenceNode("assert_that");
                    }
                })
            )
        ),
        Map.entry(
            NamespaceName.fromParts("stdlib", "matchers"),
            Map.ofEntries(
                Map.entry("equalTo", new PythonMacro() {
                    @Override
                    public PythonExpressionNode compileReceiver(PythonCodeGeneratorContext context) {
                        context.addImport(List.of("precisely", "equal_to"));
                        return new PythonReferenceNode("equal_to");
                    }
                })
            )
        )
    );

    private static Optional<PythonMacro> lookupMacro(Type type) {
        if (type instanceof StaticFunctionType staticFunctionType) {
            var macro = STATIC_FUNCTION_MACROS.getOrDefault(staticFunctionType.namespaceName(), Map.of())
                .get(staticFunctionType.functionName());
            return Optional.ofNullable(macro);
        } else {
            return Optional.empty();
        }
    }

    private static PythonExpressionNode compileAdd(TypedIntAddNode node, PythonCodeGeneratorContext context) {
        return new PythonAddNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static PythonStatementNode compileBlankLine(TypedBlankLineNode node, PythonCodeGeneratorContext context) {
        return new PythonBlankLineNode();
    }

    private static PythonExpressionNode compileBoolLiteral(TypedBoolLiteralNode node) {
        return new PythonBoolLiteralNode(node.value());
    }

    private static PythonExpressionNode compileCall(
        TypedCallNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonCallNode(
            compileCallReceiver(node.receiver(), context),
            node.positionalArgs().stream().map(arg -> compileExpression(arg, context)).toList(),
            List.of()
        );
    }

    private static PythonExpressionNode compileCallReceiver(
        TypedExpressionNode receiver,
        PythonCodeGeneratorContext context
    ) {
        var macro = lookupMacro(receiver.type());

        if (macro.isPresent()) {
            return macro.get().compileReceiver(context);
        } else {
            return compileExpression(receiver, context);
        }
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
                        List.of(),
                        List.of()
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
            public PythonExpressionNode visit(TypedCallNode node) {
                return compileCall(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedIndexNode node) {
                return compileIndex(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedIntAddNode node) {
                return compileAdd(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedIntLiteralNode node) {
                return compileIntLiteral(node);
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
            public PythonExpressionNode visit(TypedLogicalOrNode node) {
                return compileLogicalOr(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedMemberAccessNode node) {
                return compileMemberAccess(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedMemberReferenceNode node) {
                return compileMemberReference(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedStringEqualsNode node) {
                return compileStringEquals(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedStringLiteralNode node) {
                return compileStringLiteral(node);
            }
        });
    }

    private static PythonStatementNode compileExpressionStatement(
        TypedExpressionStatementNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonExpressionStatementNode(compileExpression(node.expression(), context));
    }

    private static PythonStatementNode compileFunction(TypedFunctionNode node, PythonCodeGeneratorContext context) {
        return new PythonFunctionNode(
            camelCaseToSnakeCase(node.name()),
            List.of(),
            node.params().stream().map(param -> compileParam(param)).toList(),
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
        var macro = lookupMacro(import_.type());
        if (macro.isPresent()) {
            return List.of();
        } else if (import_.fieldName().isPresent()) {
            return List.of(new PythonImportFromNode(
                namespaceNameToModuleName(import_.namespaceName()),
                List.of(import_.fieldName().get())
            ));
        } else if (import_.namespaceName().parts().size() == 1) {
            return List.of(new PythonImportNode(
                namespaceNameToModuleName(import_.namespaceName())
            ));
        } else {
            var parts = import_.namespaceName().parts();

            return List.of(new PythonImportFromNode(
                namespaceNameToModuleName(parts.subList(0, parts.size() - 1)),
                List.of(parts.get(parts.size() - 1))
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

    private static PythonStatementNode compileInterface(TypedInterfaceNode node, PythonCodeGeneratorContext context) {
        return new PythonClassDeclarationNode(node.name(), List.of(), List.of(), List.of());
    }

    private static PythonExpressionNode compileIntLiteral(TypedIntLiteralNode node) {
        return new PythonIntLiteralNode(BigInteger.valueOf(node.value()));
    }

    private static PythonExpressionNode compileLocalReference(TypedLocalReferenceNode node) {
        return new PythonReferenceNode(node.name());
    }

    private static PythonExpressionNode compileLogicalAnd(TypedLogicalAndNode node, PythonCodeGeneratorContext context) {
        return new PythonBoolAndNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static PythonExpressionNode compileLogicalOr(TypedLogicalOrNode node, PythonCodeGeneratorContext context) {
        return new PythonBoolOrNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private static PythonExpressionNode compileMemberAccess(
        TypedMemberAccessNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonAttrAccessNode(
            compileExpression(node.receiver(), context),
            node.memberName()
        );
    }

    private static PythonExpressionNode compileMemberReference(
        TypedMemberReferenceNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonAttrAccessNode(
            new PythonReferenceNode("self"),
            node.name()
        );
    }

    public static PythonModuleNode compileNamespace(TypedNamespaceNode node) {
        var context = new PythonCodeGeneratorContext();
        var moduleName = namespaceNameToModuleName(node.name());

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
        });
    }

    private static String compileParam(TypedParamNode node) {
        return camelCaseToSnakeCase(node.name());
    }

    private static PythonStatementNode compileProperty(
        TypedPropertyNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonFunctionNode(
            node.name(),
            List.of(Python.reference("property")),
            List.of("self"),
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
            .map(field -> Python.variableType(field.name(), compileTypeLevelExpression(field.type(), context)))
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
            List.of("self", "visitor"),
            List.of(
                new PythonReturnNode(
                    new PythonCallNode(
                        new PythonAttrAccessNode(
                            new PythonReferenceNode("visitor"),
                            generateVisitMethodName(node.type())
                        ),
                        List.of(new PythonReferenceNode("self")),
                        List.of()
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

    private static List<PythonStatementNode> compileSwitch(TypedSwitchNode node, PythonCodeGeneratorContext context) {
        var visitorDeclaration = new PythonClassDeclarationNode(
            "Visitor",
            List.of(),
            List.of(),
            node.cases().stream()
                .map(switchCase -> new PythonFunctionNode(
                    generateVisitMethodName((RecordType) switchCase.type().value()),
                    List.of(),
                    List.of("self", switchCase.variableName()),
                    compileFunctionStatements(switchCase.body(), context)
                ))
                .toList()
        );

        var acceptCall = new PythonCallNode(
            new PythonAttrAccessNode(
                compileExpression(node.expression(), context),
                "accept"
            ),
            List.of(
                new PythonCallNode(
                    new PythonReferenceNode("Visitor"),
                    List.of(),
                    List.of()
                )
            ),
            List.of()
        );

        return List.of(
            visitorDeclaration,
            node.returns() ? new PythonReturnNode(acceptCall) : new PythonExpressionStatementNode(acceptCall)
        );
    }

    private static PythonStatementNode compileTest(TypedTestNode node, PythonCodeGeneratorContext context) {
        return new PythonFunctionNode(
            PythonTestNames.generateName(node.name()),
            List.of(),
            List.of(),
            compileFunctionStatements(node.body(), context)
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
                        .map(arg -> compileTypeLevelExpression(arg, context))
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
                } else if (typeLevelValue == ListTypeConstructor.INSTANCE) {
                    context.addImport(List.of("typing"));
                    return new PythonAttrAccessNode(
                        new PythonReferenceNode("typing"),
                        "List"
                    );
                } else if (typeLevelValue == OptionTypeConstructor.INSTANCE) {
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
            node.name(),
            Optional.empty(),
            Optional.of(compileExpression(node.expression(), context))
        );
    }

    private static String namespaceNameToModuleName(NamespaceName name) {
        return namespaceNameToModuleName(name.parts());
    }

    private static String namespaceNameToModuleName(List<String> parts) {
        return String.join(".", parts);
    }
}
