package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptFunctionTypeNode;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class TypeScriptSerialiserFunctionTypeTests {
    @Test
    public void canSerialiseFunctionTypeWithNoParams() {
        var node = TypeScriptFunctionTypeNode.builder()
            .withReturnType(TypeScript.reference("boolean"))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("""
            () => boolean"""
        ));
    }

    @Test
    public void canSerialiseFunctionTypeWithOneParam() {
        var node = TypeScriptFunctionTypeNode.builder()
            .addParam(TypeScript.param("x", TypeScript.reference("number")))
            .withReturnType(TypeScript.reference("boolean"))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(x: number) => boolean"));
    }

    @Test
    public void canSerialiseArrowFunctionTypeWithMultipleParams() {
        var node = TypeScriptFunctionTypeNode.builder()
            .addParam(TypeScript.param("x", TypeScript.reference("number")))
            .addParam(TypeScript.param("y", TypeScript.reference("string")))
            .withReturnType(TypeScript.reference("boolean"))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(x: number, y: string) => boolean"));
    }
}
