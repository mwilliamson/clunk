package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserLogicalNotTests {
    @Test
    public void canSerialiseLogicalNot() {
        var node = Java.logicalNot(Java.reference("a"));

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("!a"));
    }
}
