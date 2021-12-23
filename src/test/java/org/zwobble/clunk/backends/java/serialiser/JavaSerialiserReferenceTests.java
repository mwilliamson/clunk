package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserReferenceTests {
    @Test
    public void isSerialisedToName() {
        var node = Java.reference("example");

        var result = serialiseToString(node, JavaSerialiser::serialiseExpression);

        assertThat(result, equalTo("example"));
    }
}
