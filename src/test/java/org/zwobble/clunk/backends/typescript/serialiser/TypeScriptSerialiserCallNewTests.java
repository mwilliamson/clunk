package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserCallNewTests {
    @Test
    public void canSerialiseCallWithNoArguments() {
        var node = TypeScript.callNew(TypeScript.reference("X"), List.of());

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("new X()"));
    }

    @Test
    public void canSerialiseCallWithOneArgument() {
        var node = TypeScript.callNew(TypeScript.reference("X"), List.of(TypeScript.boolFalse()));

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("new X(false)"));
    }

    @Test
    public void canSerialiseCallWithMultipleArguments() {
        var node = TypeScript.callNew(TypeScript.reference("X"), List.of(TypeScript.boolFalse(), TypeScript.boolTrue()));

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("new X(false, true)"));
    }
}
