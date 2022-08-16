package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedSwitchNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorSwitchTests {
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
            .returns(true)
            .build();

        var result = JavaCodeGenerator.compileFunctionStatement(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
            return node.accept(new Node.Visitor<>() {
                @Override
                public example.Add visit(Add add) {
                    return add;
                }
            });
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
            .returns(false)
            .build();

        var result = JavaCodeGenerator.compileFunctionStatement(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
            node.accept(new Node.Visitor<>() {
                @Override
                public Void visit(Add add) {
                    add;
                    return null;
                }
            });
            """));
    }
}