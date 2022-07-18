package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserSubscriptionTests {
    @Test
    public void canSerialiseSubscriptionWithOneArg() {
        var node = Python.subscription(Python.reference("x"), List.of(Python.reference("y")));

        var result = serialiseToString(node, PythonSerialiser::serialiseExpression);

        assertThat(result, equalTo("x[y]"));
    }

    @Test
    public void canSerialiseSubscriptionWithMultipleArgs() {
        var node = Python.subscription(Python.reference("x"), List.of(Python.reference("y"), Python.reference("z")));

        var result = serialiseToString(node, PythonSerialiser::serialiseExpression);

        assertThat(result, equalTo("x[y, z]"));
    }
}
