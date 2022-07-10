package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.python.ast.*;
import org.zwobble.clunk.typechecker.FieldsLookup;
import org.zwobble.clunk.types.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            public PythonExpressionNode visit(TypedIntLiteralNode node) {
                return compileIntLiteral(node);
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
            node.params().stream().map(param -> compileParam(param)).toList(),
            node.body().stream().map(statement -> compileFunctionStatement(statement, context)).toList()
        );
    }

    public PythonStatementNode compileFunctionStatement(TypedFunctionStatementNode node, PythonCodeGeneratorContext context) {
        return node.accept(new TypedFunctionStatementNode.Visitor<PythonStatementNode>() {
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
        return new PythonClassDeclarationNode(node.name(), List.of(), List.of());
    }

    private PythonExpressionNode compileIntLiteral(TypedIntLiteralNode node) {
        return new PythonIntLiteralNode(BigInteger.valueOf(node.value()));
    }

    public PythonModuleNode compileNamespace(TypedNamespaceNode node, FieldsLookup fieldsLookup) {
        var context = new PythonCodeGeneratorContext(fieldsLookup);
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

        var statements = context.fieldsOf(node.type()).stream()
            .map(field -> Python.variableType(field.name(), compileTypeLevelExpression(field.type(), context)))
            .toList();

        return new PythonClassDeclarationNode(node.name(), decorators, statements);
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
            node.body().stream().map(statement -> compileFunctionStatement(statement, context)).toList()
        );
    }

    public PythonExpressionNode compileTypeLevelExpression(
        TypedTypeLevelExpressionNode node,
        PythonCodeGeneratorContext context
    ) {
        return compileTypeLevelValue(node.value(), context);
    }

    private PythonExpressionNode compileTypeLevelValue(TypeLevelValue type, PythonCodeGeneratorContext context) {
        if (type == BoolType.INSTANCE) {
            return new PythonReferenceNode("bool");
        } else if (type == IntType.INSTANCE) {
            return new PythonReferenceNode("int");
        } else if (type instanceof InterfaceType interfaceType) {
            return new PythonReferenceNode(interfaceType.name());
        } else if (type instanceof ListType listType) {
            context.addImport(List.of("typing"));
            return new PythonSubscriptionNode(
                new PythonAttrAccessNode(
                    new PythonReferenceNode("typing"),
                    "List"
                ),
                compileTypeLevelValue(listType.elementType(), context)
            );
        } else if (type instanceof OptionType optionType) {
            context.addImport(List.of("typing"));
            return new PythonSubscriptionNode(
                new PythonAttrAccessNode(
                    new PythonReferenceNode("typing"),
                    "Optional"
                ),
                compileTypeLevelValue(optionType.elementType(), context)
            );
        } else if (type instanceof RecordType recordType) {
            return new PythonReferenceNode(recordType.name());
        } else if (type == StringType.INSTANCE) {
            return new PythonReferenceNode("str");
        } else {
            throw new RuntimeException("TODO");
        }
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
