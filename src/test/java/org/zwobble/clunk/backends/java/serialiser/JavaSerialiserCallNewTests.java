package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;
import org.zwobble.clunk.backends.java.ast.JavaCallNewNode;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserCallNewTests {
    @Test
    public void canSerialiseCallWithNoArguments() {
        var node = Java.callNew(Java.reference("X"), List.of());

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("new X()"));
    }

    @Test
    public void canSerialiseCallWithOneArgument() {
        var node = Java.callNew(Java.reference("X"), List.of(Java.boolFalse()));

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("new X(false)"));
    }

    @Test
    public void canSerialiseCallWithMultipleArguments() {
        var node = Java.callNew(Java.reference("X"), List.of(Java.boolFalse(), Java.boolTrue()));

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("new X(false, true)"));
    }

    @Test
    public void canSerialiseCallWithInferredTypeArguments() {
        var node = JavaCallNewNode.builder(Java.reference("X"))
            .inferTypeArgs()
            .build();

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("new X<>()"));
    }

    @Test
    public void canSerialiseCallWithOneTypeArgument() {
        var node = JavaCallNewNode.builder(Java.reference("X"))
            .addTypeArg(Java.typeVariableReference("A"))
            .build();

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("new X<A>()"));
    }

    @Test
    public void canSerialiseCallWithMultipleTypeArguments() {
        var node = JavaCallNewNode.builder(Java.reference("X"))
            .addTypeArg(Java.typeVariableReference("A"))
            .addTypeArg(Java.typeVariableReference("B"))
            .build();

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("new X<A, B>()"));
    }
}
