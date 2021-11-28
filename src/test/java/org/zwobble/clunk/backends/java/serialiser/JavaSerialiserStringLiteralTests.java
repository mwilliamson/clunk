package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserStringLiteralTests {
    @Test
    public void emptyString() {
        var node = Java.string("");

        var result = serialiseToString(node, JavaSerialiser::serialiseExpression);

        assertThat(result, equalTo("\"\""));
    }

    @Test
    public void stringOfAsciiCharacters() {
        var node = Java.string("abcXYZ123");

        var result = serialiseToString(node, JavaSerialiser::serialiseExpression);

        assertThat(result, equalTo("\"abcXYZ123\""));
    }

    @Test
    public void specialCharactersAreEscaped() {
        var node = Java.string("\b\t\n\f\r\"\\");

        var result = serialiseToString(node, JavaSerialiser::serialiseExpression);

        assertThat(result, equalTo("\"\\b\\t\\n\\f\\r\\\"\\\\\""));
    }
}
