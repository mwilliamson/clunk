package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserExpressionPrecedenceTests {
    @Test
    public void subExpressionIsParenthesizedWhenOfLowerPrecedence() {
        var node = Java.call(
            Java.add(Java.reference("a"), Java.reference("b")),
            List.of()
        );

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(a + b)()"));
    }

    @Test
    public void subExpressionIsNotParenthesizedWhenOfSamePrecedence() {
        var node = Java.add(
            Java.add(Java.reference("a"), Java.reference("b")),
            Java.reference("c")
        );

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("a + b + c"));
    }

    @Test
    public void subExpressionIsNotParenthesizedWhenOfHigherPrecedence() {
        var node = Java.add(
            Java.call(Java.reference("a"), List.of()),
            Java.reference("b")
        );

        var result = serialiseToString(node, JavaSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("a() + b"));
    }
}
