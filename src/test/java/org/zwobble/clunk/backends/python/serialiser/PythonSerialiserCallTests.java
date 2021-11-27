package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserCallTests {
    @Test
    public void isSerialisedToName() {
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
}
