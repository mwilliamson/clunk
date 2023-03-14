package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserCallTests {
    @Test
    public void canSerialiseCallWithNoArguments() {
        var node = TypeScript.call(TypeScript.reference("f"), List.of());

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("f()"));
    }

    @Test
    public void canSerialiseCallWithOneArgument() {
        var node = TypeScript.call(TypeScript.reference("f"), List.of(TypeScript.boolFalse()));

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("f(false)"));
    }

    @Test
    public void canSerialiseCallWithMultipleArguments() {
        var node = TypeScript.call(TypeScript.reference("f"), List.of(TypeScript.boolFalse(), TypeScript.boolTrue()));

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("f(false, true)"));
    }
}
