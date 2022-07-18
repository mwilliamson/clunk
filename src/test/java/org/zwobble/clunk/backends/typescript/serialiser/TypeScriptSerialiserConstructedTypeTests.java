package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserConstructedTypeTests {
    @Test
    public void canSerialiseWithOneArgument() {
        var node = TypeScript.constructedType(TypeScript.reference("A"), List.of(TypeScript.reference("B")));

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(A)<B>"));
    }

    @Test
    public void canSerialiseWithMultipleArguments() {
        var node = TypeScript.constructedType(TypeScript.reference("A"), List.of(TypeScript.reference("B"), TypeScript.reference("C")));

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(A)<B, C>"));
    }
}
