package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedFunctionNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.IntType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorFunctionTests {
    @Test
    public void nameIsConvertedToSnakeCase() {
        var node = TypedFunctionNode.builder()
            .name("makeItSo")
            .build();

        var result = PythonCodeGenerator.compileNamespaceStatement(node);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                def make_it_so():
                    pass
                """
        ));
    }

    @Test
    public void argNamesAreConvertedToSnakeCase() {
        var node = TypedFunctionNode.builder()
            .name("f")
            .addArg(Typed.arg("maxWidth", IntType.INSTANCE))
            .addArg(Typed.arg("maxHeight", IntType.INSTANCE))
            .build();

        var result = PythonCodeGenerator.compileNamespaceStatement(node);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                def f(max_width, max_height):
                    pass
                """
        ));
    }
}
