package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserIntLiteralTests {
    @Test
    public void canSerialiseZero() {
        var node = TypeScript.numberLiteral(0);

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("0"));
    }

    @Test
    public void canSerialisePositiveIntegers() {
        var node = TypeScript.numberLiteral(123);

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("123"));
    }

    @Test
    public void canSerialiseNegativeIntegers() {
        var node = TypeScript.numberLiteral(-123);

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("-123"));
    }

    @Test
    public void canSerialiseFractionalNumbers() {
        var node = TypeScript.numberLiteral(0.5);

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("0.5"));
    }
}
