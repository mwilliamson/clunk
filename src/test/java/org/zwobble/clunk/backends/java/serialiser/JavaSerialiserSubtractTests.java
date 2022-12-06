package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserSubtractTests {
    @Test
    public void canSerialiseSubtract() {
        var node = Java.subtract(
            Java.reference("a"),
            Java.reference("b")
        );

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("a - b"));
    }
}
