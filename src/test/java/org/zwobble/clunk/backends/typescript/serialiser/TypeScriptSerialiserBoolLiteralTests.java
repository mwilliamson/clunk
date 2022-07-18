package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserBoolLiteralTests {
    @Test
    public void falseIsSerialisedToFalseKeyword() {
        var node = TypeScript.boolFalse();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("false"));
    }

    @Test
    public void trueIsSerialisedToTrueKeyword() {
        var node = TypeScript.boolTrue();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("true"));
    }
}
