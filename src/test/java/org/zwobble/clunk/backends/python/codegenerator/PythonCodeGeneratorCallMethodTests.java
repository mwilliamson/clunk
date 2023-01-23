package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallMethodNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.types.NamespaceName;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorCallMethodTests {
    @Test
    public void callToMethodsWithExplicitReceiverAreCompiledToCallsWithExplicitReceiver() {
        var node = TypedCallMethodNode.builder()
            .receiver(Typed.localReference(
                "x",
                Types.recordType(NamespaceName.fromParts("example"), "X")
            ))
            .methodName("y")
            .positionalArgs(List.of(Typed.intLiteral(123)))
            .type(Types.INT)
            .build();

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("x.y(123)"));
    }

    @Test
    public void callToMethodsWithImplicitReceiverAreCompiledToCallsWithExplicitSelfReceiver() {
        var node = TypedCallMethodNode.builder()
            .methodName("y")
            .positionalArgs(List.of(Typed.intLiteral(123)))
            .type(Types.INT)
            .build();

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("self.y(123)"));
    }

    @Test
    public void methodNamesArePythonized() {
        var node = TypedCallMethodNode.builder()
            .receiver(Typed.localReference(
                "x",
                Types.recordType(NamespaceName.fromParts("example"), "X")
            ))
            .methodName("isValid")
            .positionalArgs(List.of())
            .type(Types.BOOL)
            .build();

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("x.is_valid()"));
    }

    @Test
    public void whenReceiverTypeHasMacroThenMethodCallIsCompiledUsingMacro() {
        var node = TypedCallMethodNode.builder()
            .receiver(Typed.localReference(
                "builder",
                Types.STRING_BUILDER
            ))
            .methodName("build")
            .positionalArgs(List.of())
            .type(Types.UNIT)
            .build();

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("\"\".join(builder)"));
    }
}
