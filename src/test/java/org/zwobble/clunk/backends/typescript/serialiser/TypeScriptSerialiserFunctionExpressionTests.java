package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptFunctionExpressionNode;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserFunctionExpressionTests {
    @Test
    public void canSerialiseEmptyFunction() {
        var node = TypeScriptFunctionExpressionNode.builder()
            .build();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("""
            function () {
            }"""
        ));
    }

    @Test
    public void canSerialiseFunctionWithOneParam() {
        var node = TypeScriptFunctionExpressionNode.builder()
            .addParam(TypeScript.param("x", TypeScript.reference("number")))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("""
            function (x: number) {
            }"""
        ));
    }

    @Test
    public void canSerialiseFunctionWithMultipleParams() {
        var node = TypeScriptFunctionExpressionNode.builder()
            .addParam(TypeScript.param("x", TypeScript.reference("number")))
            .addParam(TypeScript.param("y", TypeScript.reference("string")))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("""
            function (x: number, y: string) {
            }"""
        ));
    }

    @Test
    public void canSerialiseFunctionWithBody() {
        var node = TypeScriptFunctionExpressionNode.builder()
            .addBodyStatement(TypeScript.returnStatement(TypeScript.boolFalse()))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("""
            function () {
                return false;
            }"""
        ));
    }
}
