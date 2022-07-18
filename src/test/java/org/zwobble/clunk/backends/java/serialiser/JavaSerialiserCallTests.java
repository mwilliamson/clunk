package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserCallTests {
    @Test
    public void canSerialiseCallWithNoArguments() {
        var node = Java.call(Java.reference("f"), List.of());

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("f()"));
    }

    @Test
    public void canSerialiseCallWithOneArgument() {
        var node = Java.call(Java.reference("f"), List.of(Java.boolFalse()));

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("f(false)"));
    }

    @Test
    public void canSerialiseCallWithMultipleArguments() {
        var node = Java.call(Java.reference("f"), List.of(Java.boolFalse(), Java.boolTrue()));

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("f(false, true)"));
    }
}
