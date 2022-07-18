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

import static org.zwobble.clunk.backends.python.codegenerator.CaseConverter.camelCaseToSnakeCase;

public class PythonCodeGenerator {
    public static final PythonCodeGenerator DEFAULT = new PythonCodeGenerator();

    private interface PythonMacro {
        PythonExpressionNode compileReceiver(PythonCodeGeneratorContext context);
    }

    private static final Map<NamespaceName, Map<String, PythonMacro>> MACROS = Map.ofEntries(
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
            var macro = MACROS.getOrDefault(staticFunctionType.namespaceName(), Map.of())
                .get(staticFunctionType.functionName());
            return Optional.ofNullable(macro);
        } else {
            return Optional.empty();
        }
    }

    private PythonExpressionNode compileAdd(TypedIntAddNode node, PythonCodeGeneratorContext context) {
        return new PythonAddNode(
            compileExpression(node.left(), context),
            compileExpression(node.right(), context)
        );
    }

    private PythonStatementNode compileBlankLine(TypedBlankLineNode node, PythonCodeGeneratorContext context) {
        return new PythonBlankLineNode();
    }

    private PythonExpressionNode compileBoolLiteral(TypedBoolLiteralNode node) {
        return new PythonBoolLiteralNode(node.value());
    }

    private PythonExpressionNode compileCall(
        TypedCallNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonCallNode(
            compileCallReceiver(node.receiver(), context),
            node.positionalArgs().stream().map(arg -> compileExpression(arg, context)).toList(),
            List.of()
        );
    }

    private PythonExpressionNode compileCallReceiver(
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

    private PythonStatementNode compileEnum(
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

    public PythonExpressionNode compileExpression(
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
            public PythonExpressionNode visit(TypedIntAddNode node) {
                return compileAdd(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedIntLiteralNode node) {
                return compileIntLiteral(node);
            }

            @Override
            public PythonExpressionNode visit(TypedMemberAccessNode node) {
                return compileMemberAccess(node, context);
            }

            @Override
            public PythonExpressionNode visit(TypedReferenceNode node) {
                return compileReference(node);
            }

            @Override
            public PythonExpressionNode visit(TypedStringLiteralNode node) {
                return compileStringLiteral(node);
            }
        });
    }

    private PythonStatementNode compileExpressionStatement(
        TypedExpressionStatementNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonExpressionStatementNode(compileExpression(node.expression(), context));
    }

    private PythonStatementNode compileFunction(TypedFunctionNode node, PythonCodeGeneratorContext context) {
        return new PythonFunctionNode(
            camelCaseToSnakeCase(node.name()),
            List.of(),
            node.params().stream().map(param -> compileParam(param)).toList(),
            node.body().stream().map(statement -> compileFunctionStatement(statement, context)).toList()
        );
    }

    public PythonStatementNode compileFunctionStatement(TypedFunctionStatementNode node, PythonCodeGeneratorContext context) {
        return node.accept(new TypedFunctionStatementNode.Visitor<PythonStatementNode>() {
            @Override
            public PythonStatementNode visit(TypedBlankLineNode node) {
                return compileBlankLine(node, context);
            }

            @Override
            public PythonStatementNode visit(TypedExpressionStatementNode node) {
                return compileExpressionStatement(node, context);
            }

            @Override
            public PythonStatementNode visit(TypedIfStatementNode node) {
                return compileIfStatement(node, context);
            }

            @Override
            public PythonStatementNode visit(TypedReturnNode node) {
                return compileReturn(node, context);
            }

            @Override
            public PythonStatementNode visit(TypedVarNode node) {
                return compileVar(node, context);
            }
        });
    }

    private PythonStatementNode compileIfStatement(
        TypedIfStatementNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonIfStatementNode(
            node.conditionalBranches().stream()
                .map(conditionalBranch -> new PythonConditionalBranchNode(
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

    private List<PythonStatementNode> compileImport(TypedImportNode import_) {
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

    private PythonStatementNode compileInterface(TypedInterfaceNode node, PythonCodeGeneratorContext context) {
        return new PythonClassDeclarationNode(node.name(), List.of(), List.of(), List.of());
    }

    private PythonExpressionNode compileIntLiteral(TypedIntLiteralNode node) {
        return new PythonIntLiteralNode(BigInteger.valueOf(node.value()));
    }

    private PythonExpressionNode compileMemberAccess(
        TypedMemberAccessNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonAttrAccessNode(
            compileExpression(node.receiver(), context),
            node.memberName()
        );
    }

    public PythonModuleNode compileNamespace(TypedNamespaceNode node) {
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

    public PythonStatementNode compileNamespaceStatement(
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
            public PythonStatementNode visit(TypedTestNode node) {
                return compileTest(node, context);
            }
        });
    }

    private String compileParam(TypedParamNode node) {
        return camelCaseToSnakeCase(node.name());
    }

    private PythonStatementNode compileProperty(
        TypedPropertyNode node,
        PythonCodeGeneratorContext context
    ) {
        return new PythonFunctionNode(
            node.name(),
            List.of(Python.reference("property")),
            List.of("self"),
            node.body().stream()
                .map(statement -> compileFunctionStatement(statement, context))
                .toList()
        );
    }

    public PythonClassDeclarationNode compileRecord(
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

        return new PythonClassDeclarationNode(node.name(), decorators, List.of(), body);
    }

    private PythonStatementNode compileRecordBodyDeclaration(
        TypedRecordBodyDeclarationNode node,
        PythonCodeGeneratorContext context
    ) {
        return node.accept(new TypedRecordBodyDeclarationNode.Visitor<PythonStatementNode>() {
            @Override
            public PythonStatementNode visit(TypedPropertyNode node) {
                return compileProperty(node, context);
            }
        });
    }

    private PythonExpressionNode compileReference(TypedReferenceNode node) {
        return new PythonReferenceNode(node.name());
    }

    private PythonStatementNode compileReturn(TypedReturnNode node, PythonCodeGeneratorContext context) {
        return new PythonReturnNode(compileExpression(node.expression(), context));
    }

    private PythonExpressionNode compileStringLiteral(TypedStringLiteralNode node) {
        return new PythonStringLiteralNode(node.value());
    }

    private PythonStatementNode compileTest(TypedTestNode node, PythonCodeGeneratorContext context) {
        return new PythonFunctionNode(
            PythonTestNames.generateName(node.name()),
            List.of(),
            List.of(),
            node.body().stream().map(statement -> compileFunctionStatement(statement, context)).toList()
        );
    }

    public PythonExpressionNode compileTypeLevelExpression(
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

    private PythonStatementNode compileVar(TypedVarNode node, PythonCodeGeneratorContext context) {
        return new PythonAssignmentNode(
            node.name(),
            Optional.empty(),
            Optional.of(compileExpression(node.expression(), context))
        );
    }

    private String namespaceNameToModuleName(NamespaceName name) {
        return namespaceNameToModuleName(name.parts());
    }

    private String namespaceNameToModuleName(List<String> parts) {
        return String.join(".", parts);
    }
}
