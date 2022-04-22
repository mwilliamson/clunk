package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserCallTests {
    @Test
    public void canSerialiseCallWithNoArguments() {
        var node = Python.call(
            Python.reference("f"),
            List.of(),
            List.of()
        );

        var result = serialiseToString(node, PythonSerialiser::serialiseExpression);

        assertThat(result, equalTo("(f)()"));
    }

    @Test
    public void canSerialiseCallWithPositionalArguments() {
        var node = Python.call(
            Python.reference("f"),
            List.of(
                Python.reference("x1"),
                Python.reference("y2")
            ),
            List.of()
        );

        var result = serialiseToString(node, PythonSerialiser::serialiseExpression);

        assertThat(result, equalTo("(f)(x1, y2)"));
    }

    @Test
    public void canSerialiseCallWithKeywordArguments() {
        var node = Python.call(
            Python.reference("f"),
            List.of(
                Python.kwarg("x", Python.reference("x1")),
                Python.kwarg("y", Python.reference("y2"))
            )
        );

        var result = serialiseToString(node, PythonSerialiser::serialiseExpression);

        assertThat(result, equalTo("(f)(x=x1, y=y2)"));
    }

    @Test
    public void canSerialiseCallWithBothPositionalAndKeywordArguments() {
        var node = Python.call(
            Python.reference("f"),
            List.of(
                Python.intLiteral(123),
                Python.intLiteral(456)
            ),
            List.of(
                Python.kwarg("x", Python.reference("x1")),
                Python.kwarg("y", Python.reference("y2"))
            )
        );

        var result = serialiseToString(node, PythonSerialiser::serialiseExpression);

        assertThat(result, equalTo("(f)(123, 456, x=x1, y=y2)"));
    }
}
