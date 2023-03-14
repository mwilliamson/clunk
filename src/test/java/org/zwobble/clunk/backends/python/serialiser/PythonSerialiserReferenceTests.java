package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserReferenceTests {
    @Test
    public void isSerialisedToName() {
        var node = Python.reference("Example");

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("Example"));
    }
}
