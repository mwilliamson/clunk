package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserStringLiteralTests {
    @Test
    public void emptyString() {
        var node = TypeScript.string("");

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("\"\""));
    }

    @Test
    public void stringOfAsciiCharacters() {
        var node = TypeScript.string("abcXYZ123");

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("\"abcXYZ123\""));
    }

    @Test
    public void specialCharactersAreEscaped() {
        var node = TypeScript.string("\b\t\n\013\f\r\"\\");

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("\"\\b\\t\\n\\v\\f\\r\\\"\\\\\""));
    }
}
