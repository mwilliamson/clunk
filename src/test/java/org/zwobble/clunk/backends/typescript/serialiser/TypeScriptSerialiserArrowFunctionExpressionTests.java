package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptArrowFunctionExpressionNode;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class TypeScriptSerialiserArrowFunctionExpressionTests {
    @Test
    public void canSerialiseArrowFunctionWithNoParams() {
        var node = TypeScriptArrowFunctionExpressionNode.builder()
            .withBodyExpression(TypeScript.numberLiteral(42))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("""
            () => 42"""
        ));
    }

    @Test
    public void canSerialiseArrowFunctionWithOneParam() {
        var node = TypeScriptArrowFunctionExpressionNode.builder()
            .addParam(TypeScript.param("x", TypeScript.reference("number")))
            .withBodyExpression(TypeScript.numberLiteral(42))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(x: number) => 42"));
    }

    @Test
    public void canSerialiseArrowFunctionWithMultipleParams() {
        var node = TypeScriptArrowFunctionExpressionNode.builder()
            .addParam(TypeScript.param("x", TypeScript.reference("number")))
            .addParam(TypeScript.param("y", TypeScript.reference("string")))
            .withBodyExpression(TypeScript.numberLiteral(42))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(x: number, y: string) => 42"));
    }

    @Test
    public void canSerialiseArrowFunctionWithExplicitParamType() {
        var node = TypeScriptArrowFunctionExpressionNode.builder()
            .addParam(TypeScript.param("x", TypeScript.reference("number")))
            .withBodyExpression(TypeScript.numberLiteral(42))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(x: number) => 42"));
    }

    @Test
    public void canSerialiseArrowFunctionWithImplicitParamType() {
        var node = TypeScriptArrowFunctionExpressionNode.builder()
            .addParam(TypeScript.param("x"))
            .withBodyExpression(TypeScript.numberLiteral(42))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiserTesting::serialiseExpression);

        assertThat(result, equalTo("(x) => 42"));
    }
}
