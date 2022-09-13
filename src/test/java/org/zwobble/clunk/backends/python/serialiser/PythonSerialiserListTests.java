package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserListTests {
    @Test
    public void canSerialiseEmptyList() {
        var node = Python.list(List.of());

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("[]"));
    }

    @Test
    public void canSerialiseSingletonList() {
        var node = Python.list(List.of(Python.intLiteral(1)));

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("[1]"));
    }

    @Test
    public void canSerialiseListWithMultipleElements() {
        var node = Python.list(List.of(Python.intLiteral(1), Python.intLiteral(2), Python.intLiteral(3)));

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("[1, 2, 3]"));
    }
}
