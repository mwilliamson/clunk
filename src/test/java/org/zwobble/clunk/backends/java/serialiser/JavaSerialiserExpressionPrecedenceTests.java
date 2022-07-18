package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
}
