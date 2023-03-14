package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserParameterizedTypeTests {
    @Test
    public void canSerialiseWithOneArgument() {
        var node = Java.parameterizedType(Java.typeVariableReference("A"), List.of(Java.typeVariableReference("B")));

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeExpression);

        assertThat(result, equalTo("A<B>"));
    }

    @Test
    public void canSerialiseWithMultipleArguments() {
        var node = Java.parameterizedType(Java.typeVariableReference("A"), List.of(Java.typeVariableReference("B"), Java.typeVariableReference("C")));

        var result = serialiseToString(node, JavaSerialiser::serialiseTypeExpression);

        assertThat(result, equalTo("A<B, C>"));
    }
}
