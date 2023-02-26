package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallMethodNode;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiserTesting;
import org.zwobble.clunk.types.NamespaceId;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorCallMethodTests {
    @Test
    public void callToMethodsWithExplicitReceiverAreCompiledToCallsWithExplicitReceiver() {
        var node = TypedCallMethodNode.builder()
            .receiver(Typed.localReference(
                "x",
                Types.recordType(NamespaceId.source("example"), "X")
            ))
            .methodName("y")
            .positionalArgs(List.of(Typed.intLiteral(123)))
            .type(Types.INT)
            .build();

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("x.y(123)"));
    }

    @Test
    public void callToMethodsWithImplicitReceiverAreCompiledToCallsWithImplicitReceiver() {
        var node = TypedCallMethodNode.builder()
            .methodName("y")
            .positionalArgs(List.of(Typed.intLiteral(123)))
            .type(Types.INT)
            .build();

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("y(123)"));
    }

    @Test
    public void whenReceiverTypeHasMacroThenMethodCallIsCompiledUsingMacro() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "length",
            List.of(),
            Types.INT
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.size()"));
    }
}
