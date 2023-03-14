package org.zwobble.clunk.backends.python.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.python.ast.Python;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonSerialiserForEachTests {
    @Test
    public void canSerialiseForEach() {
        var node = Python.forEach(
            "x",
            Python.reference("xs"),
            List.of(
                Python.expressionStatement(Python.call(
                    Python.reference("print"),
                    List.of(Python.reference("x")),
                    List.of()
                ))
            )
        );

        var result = serialiseToString(node, PythonSerialiser::serialiseStatement);

        assertThat(result, equalTo(
            """
            for x in xs:
                print(x)
            """
        ));
    }
}
