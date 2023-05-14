package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class JavaSerialiserConditionalTests {
    @Test
    public void canSerialiseConditionalOperator() {
        var node = Java.conditional(
            Java.reference("a"),
            Java.reference("b"),
            Java.reference("c")
        );

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("a ? b : c"));
    }
}
