package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.python.ast.Python;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.backends.python.serialiser.PythonSerialiser.serialiseReference;

public class PythonSerialiserReferenceTests {
    @Test
    public void isSerialisedToName() {
        var node = Python.reference("Example");

        var builder = new CodeBuilder();
        serialiseReference(node, builder);

        assertThat(builder.toString(), equalTo("Example"));
    }
}
