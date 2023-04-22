package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import java.util.List;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class TypeScriptSerialiserObjectLiteralTests {
    @Test
    public void canSerialiseEmptyObject() {
        var node = TypeScript.objectLiteral(List.of());

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("{}"));
    }

    @Test
    public void canSerialiseObjectWithOneProperty() {
        var node = TypeScript.objectLiteral(List.of(
            TypeScript.propertyLiteral("x", TypeScript.numberLiteral(1))
        ));

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("{x: 1}"));
    }

    @Test
    public void canSerialiseObjectWithMultipleProperties() {
        var node = TypeScript.objectLiteral(List.of(
            TypeScript.propertyLiteral("x", TypeScript.numberLiteral(1)),
            TypeScript.propertyLiteral("y", TypeScript.numberLiteral(2)),
            TypeScript.propertyLiteral("z", TypeScript.numberLiteral(3))
        ));

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("{x: 1, y: 2, z: 3}"));
    }
}
