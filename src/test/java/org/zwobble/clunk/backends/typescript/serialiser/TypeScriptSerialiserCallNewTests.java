package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
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

    @Test
    public void canSerialiseCallWithOneTypeArgument() {
        var node = TypeScript.callNew(
            TypeScript.reference("X"),
            List.of(TypeScript.reference("Y")),
            List.of()
        );

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("new X<Y>()"));
    }

    @Test
    public void canSerialiseCallWithMultipleTypeArguments() {
        var node = TypeScript.callNew(
            TypeScript.reference("X"),
            List.of(TypeScript.reference("Y"), TypeScript.reference("Z")),
            List.of()
        );

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("new X<Y, Z>()"));
    }
}
