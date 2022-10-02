package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserCastTests {
    @Test
    public void canSerialiseCast() {
        var node = Java.cast(Java.typeVariableReference("String"), Java.reference("a"));

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(String) a"));
    }
}
