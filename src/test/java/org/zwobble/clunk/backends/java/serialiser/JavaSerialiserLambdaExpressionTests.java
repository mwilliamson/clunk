package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import java.util.List;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class JavaSerialiserLambdaExpressionTests {
    @Test
    public void canSerialiseLambdaWithNoParams() {
        var node = Java.lambdaExpression(
            List.of(),
            Java.intLiteral(42)
        );

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("() -> 42"));
    }

    @Test
    public void canSerialiseLambdaWithOneParam() {
        var node = Java.lambdaExpression(
            List.of("a"),
            Java.intLiteral(42)
        );

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(a) -> 42"));
    }

    @Test
    public void canSerialiseLambdaWithMultipleParams() {
        var node = Java.lambdaExpression(
            List.of("a", "b", "c"),
            Java.intLiteral(42)
        );

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(a, b, c) -> 42"));
    }
}
