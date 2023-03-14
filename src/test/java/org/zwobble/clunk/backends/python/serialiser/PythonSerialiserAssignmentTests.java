package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;
import org.zwobble.clunk.backends.python.ast.PythonAssignmentNode;

import java.util.Optional;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserAssignmentTests {
    @Test
    public void assignmentWithTypeAndNoExpressionIsSerialised() {
        var node = new PythonAssignmentNode("message", Optional.of(Python.reference("str")), Optional.empty());

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("message: str\n"));
    }

    @Test
    public void assignmentWithExpressionAndNoTypeIsSerialised() {
        var node = new PythonAssignmentNode("message", Optional.empty(), Optional.of(Python.FALSE));

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("message = False\n"));
    }
}
