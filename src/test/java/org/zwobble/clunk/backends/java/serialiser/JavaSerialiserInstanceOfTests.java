package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserInstanceOfTests {
    @Test
    public void canSerialiseInstanceOfWithoutTarget() {
        var node = Java.instanceOf(
            Java.reference("a"),
            Java.typeVariableReference("X")
        );

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("a instanceof X"));
    }

    @Test
    public void canSerialiseInstanceOfWithTarget() {
        var node = Java.instanceOf(
            Java.reference("a"),
            Java.typeVariableReference("X"),
            "b"
        );

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("a instanceof X b"));
    }
}
