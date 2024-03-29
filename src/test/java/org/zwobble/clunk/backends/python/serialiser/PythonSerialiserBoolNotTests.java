package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserBoolNotTests {
    @Test
    public void canSerialiseBoolNot() {
        var node = Python.boolNot(Python.reference("a"));

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("not a"));
    }
}
