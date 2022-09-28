package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserListComprehensionTests {
    @Test
    public void canSerialiseComprehension() {
        var node = Python.listComprehension(
            Python.add(Python.reference("x"), Python.intLiteral(1)),
            List.of(
                Python.comprehensionForClause("x", Python.reference("xs"))
            )
        );

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("[x + 1 for x in xs]"));
    }
}
