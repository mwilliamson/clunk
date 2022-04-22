package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserIntLiteralTests {
    @Test
    public void canSerialiseZero() {
        var node = Java.intLiteral(0);

        var result = serialiseToString(node, JavaSerialiser::serialiseExpression);

        assertThat(result, equalTo("0"));
    }

    @Test
    public void canSerialisePositiveIntegers() {
        var node = Java.intLiteral(123);

        var result = serialiseToString(node, JavaSerialiser::serialiseExpression);

        assertThat(result, equalTo("123"));
    }

    @Test
    public void canSerialiseNegativeIntegers() {
        var node = Java.intLiteral(-123);

        var result = serialiseToString(node, JavaSerialiser::serialiseExpression);

        assertThat(result, equalTo("-123"));
    }
}
