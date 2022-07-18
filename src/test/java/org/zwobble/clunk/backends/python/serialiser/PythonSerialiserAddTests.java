package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserAddTests {
    @Test
    public void canSerialiseAdd() {
        var node = Python.add(
            Python.reference("a"),
            Python.reference("b")
        );

        var result = serialiseToString(node, PythonSerialiser::serialiseExpression);

        assertThat(result, equalTo("a + b"));
    }
}
