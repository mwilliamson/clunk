package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedSwitchNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorSwitchTests {
    @Test
    public void whenAllBranchesReturnThenSwitchStatementIsCompiledToReturnOfVisitorCall() {
        var interfaceType = Types.sealedInterfaceType(NamespaceId.source("example"), "Node");
        var recordType = Types.recordType(NamespaceId.source("example"), "Add");

        var node = TypedSwitchNode.builder(Typed.localReference("node", interfaceType))
            .addCase(Typed.switchCase(
                Typed.typeLevelReference("Add", recordType),
                List.of(Typed.returnStatement(Typed.localReference("node", recordType)))
            ))
            .returnType(recordType)
            .build();

        var result = JavaCodeGenerator.compileFunctionStatement(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiser::serialiseStatements);
        assertThat(string, equalTo(
            """
            return node.accept(new Node.Visitor<>() {
                @Override
                public example.Add visit(Add node) {
                    return node;
                }
            });
            """));
    }

    @Test
    public void whenNoBranchesReturnThenSwitchStatementIsCompiledToVisitorCallExpressionStatement() {
        var interfaceType = Types.sealedInterfaceType(NamespaceId.source("example"), "Node");
        var recordType = Types.recordType(NamespaceId.source("example"), "Add");

        var node = TypedSwitchNode.builder(Typed.localReference("node", interfaceType))
            .addCase(Typed.switchCase(
                Typed.typeLevelReference("Add", recordType),
                List.of(Typed.expressionStatement(Typed.localReference("node", recordType)))
            ))
            .neverReturns()
            .build();

        var result = JavaCodeGenerator.compileFunctionStatement(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiser::serialiseStatements);
        assertThat(string, equalTo(
            """
            node.accept(new Node.Visitor<>() {
                @Override
                public Void visit(Add node) {
                    node;
                    return null;
                }
            });
            """));
    }
}
