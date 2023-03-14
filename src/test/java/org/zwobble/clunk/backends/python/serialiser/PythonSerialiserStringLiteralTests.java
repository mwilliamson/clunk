package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserStringLiteralTests {
    @Test
    public void emptyString() {
        var node = Python.string("");

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("\"\""));
    }

    @Test
    public void stringOfAsciiCharacters() {
        var node = Python.string("abcXYZ123");

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("\"abcXYZ123\""));
    }

    @Test
    public void specialCharactersAreEscaped() {
        var node = Python.string("\b\t\n\013\f\r\"\\");

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("\"\\b\\t\\n\\v\\f\\r\\\"\\\\\""));
    }
}
