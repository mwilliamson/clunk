package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserAddTests {
    @Test
    public void canSerialiseCallWithNoArguments() {
        var node = Java.add(
            Java.reference("a"),
            Java.reference("b")
        );

        var result = serialiseToString(node, JavaSerialiser::serialiseExpression);

        assertThat(result, equalTo("a + b"));
    }
}
