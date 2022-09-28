package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedSwitchNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorSwitchTests {
    @Test
    public void whenAllBranchesReturnThenSwitchStatementIsCompiledToReturnOfVisitorCall() {
        var interfaceType = Types.sealedInterfaceType(NamespaceName.fromParts("example"), "Node");
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "Add");

        var node = TypedSwitchNode.builder(Typed.localReference("node", interfaceType))
            .addCase(Typed.switchCase(
                Typed.typeLevelReference("Add", recordType),
                "add",
                List.of(Typed.returnStatement(Typed.localReference("add", recordType)))
            ))
            .returnType(recordType)
            .build();

        var result = PythonCodeGenerator.compileFunctionStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatements);
        assertThat(string, equalTo(
            """
            class Visitor:
                def visit_add(self, add):
                    return add
            return node.accept(Visitor())
            """));
    }

    @Test
    public void whenNoBranchesReturnThenSwitchStatementIsCompiledToVisitorCallExpressionStatement() {
        var interfaceType = Types.sealedInterfaceType(NamespaceName.fromParts("example"), "Node");
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "Add");

        var node = TypedSwitchNode.builder(Typed.localReference("node", interfaceType))
            .addCase(Typed.switchCase(
                Typed.typeLevelReference("Add", recordType),
                "add",
                List.of(Typed.expressionStatement(Typed.localReference("add", recordType)))
            ))
            .neverReturns()
            .build();

        var result = PythonCodeGenerator.compileFunctionStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatements);
        assertThat(string, equalTo(
            """
            class Visitor:
                def visit_add(self, add):
                    add
            node.accept(Visitor())
            """));
    }

    @Test
    public void visitArgsHavePythonizedNames() {
        var interfaceType = Types.sealedInterfaceType(NamespaceName.fromParts("example"), "Node");
        var recordType = Types.recordType(NamespaceName.fromParts("example"), "IntLiteral");

        var node = TypedSwitchNode.builder(Typed.localReference("node", interfaceType))
            .addCase(Typed.switchCase(
                Typed.typeLevelReference("IntLiteral", recordType),
                "intLiteral",
                List.of(Typed.returnStatement(Typed.localReference("intLiteral", recordType)))
            ))
            .returnType(recordType)
            .build();

        var result = PythonCodeGenerator.compileFunctionStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatements);
        assertThat(string, equalTo(
            """
            class Visitor:
                def visit_int_literal(self, int_literal):
                    return int_literal
            return node.accept(Visitor())
            """));
    }
}
