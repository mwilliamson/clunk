package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserSingleLineCommentTests {
    @Test
    public void expressionIsSerialisedAsStatement() {
        var node = Python.singleLineComment(" Beware.");

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo("# Beware.\n"));
    }
}
