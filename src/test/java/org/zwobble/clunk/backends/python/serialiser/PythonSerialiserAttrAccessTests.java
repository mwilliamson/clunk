package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.CodeBuilder;
import org.zwobble.clunk.backends.python.ast.Python;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.backends.python.serialiser.PythonSerialiser.serialiseExpression;

public class PythonSerialiserAttrAccessTests {
    @Test
    public void isSerialisedToName() {
        var node = Python.attr(Python.reference("Example"), "name");

        var builder = new CodeBuilder();
        serialiseExpression(node, builder);

        assertThat(builder.toString(), equalTo("(Example).name"));
    }
}
