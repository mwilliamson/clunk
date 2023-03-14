package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserExpressionPrecedenceTests {
    @Test
    public void subExpressionIsParenthesizedWhenOfLowerPrecedence() {
        var node = Python.call(
            Python.add(Python.reference("a"), Python.reference("b")),
            List.of()
        );

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(a + b)()"));
    }

    @Test
    public void subExpressionIsNotParenthesizedWhenOfSamePrecedence() {
        var node = Python.add(
            Python.add(Python.reference("a"), Python.reference("b")),
            Python.reference("c")
        );

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("a + b + c"));
    }

    @Test
    public void subExpressionIsNotParenthesizedWhenOfHigherPrecedence() {
        var node = Python.add(
            Python.call(Python.reference("a"), List.of()),
            Python.reference("b")
        );

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("a() + b"));
    }
}
