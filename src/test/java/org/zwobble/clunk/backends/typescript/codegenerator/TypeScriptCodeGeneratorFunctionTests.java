package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedFunctionNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorFunctionTests {
    @Test
    public void functionIsCompiledToTypeScriptFunctionDeclaration() {
        var node = TypedFunctionNode.builder()
            .name("f")
            .addParam(Typed.param("x", Typed.typeLevelString()))
            .addParam(Typed.param("y", Typed.typeLevelInt()))
            .returnType(Typed.typeLevelBool())
            .addBodyStatement(Typed.returnStatement(Typed.boolFalse()))
            .build();

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                function f(x: string, y: number): boolean {
                    return false;
                }"""
        ));
    }
}
