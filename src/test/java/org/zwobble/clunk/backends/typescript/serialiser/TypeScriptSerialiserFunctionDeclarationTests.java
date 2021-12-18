package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;
import org.zwobble.clunk.backends.typescript.ast.TypeScriptFunctionDeclarationNode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserFunctionDeclarationTests {
    @Test
    public void canSerialiseEmptyFunction() {
        var node = TypeScriptFunctionDeclarationNode.builder()
            .name("f")
            .returnType(TypeScript.reference("string"))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            function f(): string {
            }"""
        ));
    }

    @Test
    public void canSerialiseFunctionWithOneParam() {
        var node = TypeScriptFunctionDeclarationNode.builder()
            .name("f")
            .addParam(TypeScript.param("x", TypeScript.reference("number")))
            .returnType(TypeScript.reference("string"))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            function f(x: number): string {
            }"""
        ));
    }

    @Test
    public void canSerialiseFunctionWithMultipleParams() {
        var node = TypeScriptFunctionDeclarationNode.builder()
            .name("f")
            .addParam(TypeScript.param("x", TypeScript.reference("number")))
            .addParam(TypeScript.param("y", TypeScript.reference("string")))
            .returnType(TypeScript.reference("string"))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            function f(x: number, y: string): string {
            }"""
        ));
    }

    @Test
    public void canSerialiseFunctionWithBody() {
        var node = TypeScriptFunctionDeclarationNode.builder()
            .name("f")
            .returnType(TypeScript.reference("string"))
            .addBodyStatement(TypeScript.returnStatement(TypeScript.boolFalse()))
            .build();

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("""
            function f(): string {
                return false;
            }"""
        ));
    }
}
