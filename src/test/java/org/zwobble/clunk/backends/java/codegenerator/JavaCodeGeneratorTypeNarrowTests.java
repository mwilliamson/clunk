package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedFunctionStatementNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorTypeNarrowTests {
    @Test
    public void typeNarrowingAssignsCastedValueToNewVariableWhichIsUsedInLaterStatements() {
        var recordType = Types.recordType(NamespaceId.source("example"), "AddNode");
        var nodes = List.<TypedFunctionStatementNode>of(
            Typed.typeNarrow("x", recordType),
            Typed.expressionStatement(Typed.localReference("x", recordType))
        );

        var result = JavaCodeGenerator.compileBlock(nodes, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiser::serialiseStatements);
        assertThat(string, equalTo("""
            var x_AddNode = (example.AddNode) x;
            x_AddNode;
            """));
    }

    @Test
    public void renamedVariableIsOnlyUsedInScope() {
        var recordType = Types.recordType(NamespaceId.source("example"), "AddNode");
        var node = Typed.ifStatement(
            List.of(Typed.conditionalBranch(Typed.boolFalse(), List.of(
                Typed.typeNarrow("x", recordType)
            ))),
            List.of(
                Typed.expressionStatement(Typed.localReference("x", Types.OBJECT))
            )
        );

        var result = JavaCodeGenerator.compileFunctionStatement(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiser::serialiseStatements);
        assertThat(string, equalTo("""
            if (false) {
                var x_AddNode = (example.AddNode) x;
            } else {
                x;
            }
            """));
    }
}
