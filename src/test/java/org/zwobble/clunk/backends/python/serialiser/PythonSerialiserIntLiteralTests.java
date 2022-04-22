package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserIntLiteralTests {
    @Test
    public void canSerialiseZero() {
        var node = Python.intLiteral(0);

        var result = serialiseToString(node, PythonSerialiser::serialiseExpression);

        assertThat(result, equalTo("0"));
    }

    @Test
    public void canSerialisePositiveIntegers() {
        var node = Python.intLiteral(123);

        var result = serialiseToString(node, PythonSerialiser::serialiseExpression);

        assertThat(result, equalTo("123"));
    }

    @Test
    public void canSerialiseNegativeIntegers() {
        var node = Python.intLiteral(-123);

        var result = serialiseToString(node, PythonSerialiser::serialiseExpression);

        assertThat(result, equalTo("-123"));
    }
}
