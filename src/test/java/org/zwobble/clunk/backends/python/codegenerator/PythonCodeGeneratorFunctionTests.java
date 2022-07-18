package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedFunctionNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorFunctionTests {
    @Test
    public void functionIsCompiledToFunction() {
        var node = TypedFunctionNode.builder()
            .name("f")
            .addParam(Typed.param("x", Typed.typeLevelString()))
            .addParam(Typed.param("y", Typed.typeLevelInt()))
            .returnType(Typed.typeLevelBool())
            .addBodyStatement(Typed.returnStatement(Typed.boolFalse()))
            .build();

        var result = PythonCodeGenerator.DEFAULT.compileNamespaceStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                def f(x, y):
                    return False
                """
        ));
    }

    @Test
    public void nameIsConvertedToSnakeCase() {
        var node = TypedFunctionNode.builder()
            .name("makeItSo")
            .build();

        var result = PythonCodeGenerator.DEFAULT.compileNamespaceStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                def make_it_so():
                    pass
                """
        ));
    }

    @Test
    public void paramNamesAreConvertedToSnakeCase() {
        var node = TypedFunctionNode.builder()
            .name("f")
            .addParam(Typed.param("maxWidth", Typed.typeLevelInt()))
            .addParam(Typed.param("maxHeight", Typed.typeLevelInt()))
            .build();

        var result = PythonCodeGenerator.DEFAULT.compileNamespaceStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                def f(max_width, max_height):
                    pass
                """
        ));
    }
}
