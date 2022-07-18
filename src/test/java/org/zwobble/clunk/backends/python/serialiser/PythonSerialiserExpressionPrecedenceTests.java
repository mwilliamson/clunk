package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
}
