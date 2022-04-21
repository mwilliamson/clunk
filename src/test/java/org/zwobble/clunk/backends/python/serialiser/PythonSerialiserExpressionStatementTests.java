package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserExpressionStatementTests {
    @Test
    public void expressionIsSerialisedAsStatement() {
        var node = Python.expressionStatement(Python.FALSE);

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("False\n"));
    }
}
