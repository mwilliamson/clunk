package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.python.ast.Python;
import org.zwobble.clunk.backends.python.ast.PythonAssignmentNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PythonSerialiserAssignmentTests {
    @Test
    public void assignmentWithTypeAndNoExpressionIsSerialised() {
        var node = new PythonAssignmentNode("message", Python.reference("str"));

        var builder = new CodeBuilder();
        PythonSerialiser.serialiseStatement(node, builder);

        assertThat(builder.toString(), equalTo("message: str"));
    }
}
