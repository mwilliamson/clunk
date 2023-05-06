package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
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

    @Test
    public void canSerialiseComprehensionWithMultipleForClauses() {
        var node = Python.listComprehension(
            Python.add(Python.reference("x"), Python.reference("y")),
            List.of(
                Python.comprehensionForClause("x", Python.reference("xs")),
                Python.comprehensionForClause("y", Python.reference("ys"))
            )
        );

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("[x + y for x in xs for y in ys]"));
    }

    @Test
    public void canSerialiseComprehensionWithIfs() {
        var node = Python.listComprehension(
            Python.add(Python.reference("x"), Python.intLiteral(1)),
            List.of(
                Python.comprehensionForClause(
                    "x",
                    Python.reference("xs"),
                    List.of(Python.reference("x"), Python.reference("y"))
                )
            )
        );

        var result = serialiseToString(node, PythonSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("[x + 1 for x in xs if x if y]"));
    }
}
