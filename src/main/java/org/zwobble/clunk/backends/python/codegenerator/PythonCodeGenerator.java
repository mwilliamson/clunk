package org.zwobble.clunk.backends.python.codegenerator;

import org.zwobble.clunk.ast.typed.*;
import org.zwobble.clunk.backends.python.ast.*;
import org.zwobble.clunk.builtins.Builtins;
import org.zwobble.clunk.types.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.zwobble.clunk.backends.python.codegenerator.CaseConverter.camelCaseToSnakeCase;

public class PythonCodeGenerator {
    public static final PythonCodeGenerator DEFAULT = new PythonCodeGenerator();

    private PythonExpressionNode compileBoolLiteral(TypedBoolLiteralNode node) {
        return new PythonBoolLiteralNode(node.value());
    }

    private PythonExpressionNode compileCall(TypedCallNode node) {
        return new PythonCallNode(
            compileExpression(node.receiver()),
            node.positionalArgs().stream().map(arg -> compileExpression(arg)).toList(),
            List.of()
        );
    }

    public PythonExpressionNode compileExpression(TypedExpressionNode node) {
        return node.accept(new TypedExpressionNode.Visitor<PythonExpressionNode>() {
            @Override
            public PythonExpressionNode visit(TypedBoolLiteralNode node) {
                return compileBoolLiteral(node);
            }

            @Override
            public PythonExpressionNode visit(TypedCallNode node) {
                return compileCall(node);
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

    private PythonStatementNode compileExpressionStatement(TypedExpressionStatementNode node) {
        if (node.expression() instanceof TypedCallNode expression) {
            if (
                expression.receiver().type() instanceof StaticFunctionType receiverType &&
                receiverType.namespaceName().equals(Builtins.NAMESPACE_STDLIB_ASSERT.name())
                && receiverType.functionName().equals("isTrue")
            ) {
                return new PythonAssertNode(compileExpression(expression.positionalArgs().get(0)));
            }
        }

        return new PythonExpressionStatementNode(compileExpression(node.expression()));
    }

    private PythonStatementNode compileFunction(TypedFunctionNode node) {
        return new PythonFunctionNode(
            camelCaseToSnakeCase(node.name()),
            node.params().stream().map(param -> compileParam(param)).toList(),
            node.body().stream().map(statement -> compileFunctionStatement(statement)).toList()
        );
    }

    public PythonStatementNode compileFunctionStatement(TypedFunctionStatementNode node) {
        return node.accept(new TypedFunctionStatementNode.Visitor<PythonStatementNode>() {
            @Override
            public PythonStatementNode visit(TypedExpressionStatementNode node) {
                return compileExpressionStatement(node);
            }

            @Override
            public PythonStatementNode visit(TypedReturnNode node) {
                return compileReturn(node);
            }

            @Override
            public PythonStatementNode visit(TypedVarNode node) {
                return compileVar(node);
            }
        });
    }

    private PythonExpressionNode compileIntLiteral(TypedIntLiteralNode node) {
        return new PythonIntLiteralNode(BigInteger.valueOf(node.value()));
    }

    public PythonModuleNode compileNamespace(TypedNamespaceNode node) {
        var moduleName = String.join(".", node.name().parts());

        var statements = new ArrayList<PythonStatementNode>();
        statements.add(new PythonImportNode("dataclasses"));

        node.statements().stream()
            .map(statement -> compileNamespaceStatement(statement))
            .forEachOrdered(statements::add);

        return new PythonModuleNode(moduleName, statements);
    }

    public PythonStatementNode compileNamespaceStatement(TypedNamespaceStatementNode node) {
        return node.accept(new TypedNamespaceStatementNode.Visitor<PythonStatementNode>() {
            @Override
            public PythonStatementNode visit(TypedFunctionNode node) {
                return compileFunction(node);
            }

            @Override
            public PythonStatementNode visit(TypedRecordNode node) {
                return compileRecord(node);
            }

            @Override
            public PythonStatementNode visit(TypedTestNode node) {
                return compileTest(node);
            }
        });
    }

    private String compileParam(TypedParamNode node) {
        return camelCaseToSnakeCase(node.name());
    }

    public PythonClassDeclarationNode compileRecord(TypedRecordNode node) {
        var decorators = List.of(
            Python.call(
                Python.attr(Python.reference("dataclasses"), "dataclass"),
                List.of(Python.kwarg("frozen", Python.TRUE))
            )
        );

        var statements = node.fields().stream()
            .map(field -> Python.variableType(field.name(), compileStaticExpression(field.type())))
            .toList();

        return new PythonClassDeclarationNode(node.name(), decorators, statements);
    }

    private PythonExpressionNode compileReference(TypedReferenceNode node) {
        return new PythonReferenceNode(node.name());
    }

    private PythonStatementNode compileReturn(TypedReturnNode node) {
        return new PythonReturnNode(compileExpression(node.expression()));
    }

    public PythonReferenceNode compileStaticExpression(TypedStaticExpressionNode node) {
        return new PythonReferenceNode(compileType(node.type()));
    }

    private PythonExpressionNode compileStringLiteral(TypedStringLiteralNode node) {
        return new PythonStringLiteralNode(node.value());
    }

    private PythonStatementNode compileTest(TypedTestNode node) {
        return new PythonFunctionNode(
            PythonTestNames.generateName(node.name()),
            List.of(),
            node.body().stream().map(statement -> compileFunctionStatement(statement)).toList()
        );
    }

    private PythonStatementNode compileVar(TypedVarNode node) {
        return new PythonAssignmentNode(
            node.name(),
            Optional.empty(),
            Optional.of(compileExpression(node.expression()))
        );
    }

    private String compileType(Type type) {
        if (type == BoolType.INSTANCE) {
            return "bool";
        } else if (type == IntType.INSTANCE) {
            return "int";
        } else if (type == StringType.INSTANCE) {
            return "str";
        } else {
            throw new RuntimeException("TODO");
        }
    }
}
