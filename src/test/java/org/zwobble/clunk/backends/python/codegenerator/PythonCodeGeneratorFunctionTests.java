package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedFunctionNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class PythonCodeGeneratorFunctionTests {
    @Test
    public void functionIsCompiledToFunction() {
        var node = TypedFunctionNode.builder()
            .name("f")
            .addPositionalParam(Typed.param("x", Typed.typeLevelString()))
            .addPositionalParam(Typed.param("y", Typed.typeLevelInt()))
            .addNamedParam(Typed.param("z", Typed.typeLevelBool()))
            .returnType(Typed.typeLevelBool())
            .addBodyStatement(Typed.returnStatement(Typed.boolFalse()))
            .build();

        var result = PythonCodeGenerator.compileNamespaceStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                def f(x, y, /, *, z):
                    return False
                """
        ));
    }

    @Test
    public void nameIsConvertedToSnakeCase() {
        var node = TypedFunctionNode.builder()
            .name("makeItSo")
            .build();

        var result = PythonCodeGenerator.compileNamespaceStatement(node, PythonCodeGeneratorContext.stub());

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
            .addPositionalParam(Typed.param("maxWidth", Typed.typeLevelInt()))
            .addPositionalParam(Typed.param("maxHeight", Typed.typeLevelInt()))
            .build();

        var result = PythonCodeGenerator.compileNamespaceStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                def f(max_width, max_height, /):
                    pass
                """
        ));
    }
}
