package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserBoolLiteralTests {
    @Test
    public void falseIsSerialisedToFalseKeyword() {
        var node = Java.boolFalse();

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("false"));
    }

    @Test
    public void trueIsSerialisedToTrueKeyword() {
        var node = Java.boolTrue();

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("true"));
    }
}
