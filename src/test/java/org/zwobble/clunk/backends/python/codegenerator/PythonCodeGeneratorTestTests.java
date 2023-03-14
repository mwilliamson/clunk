package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedTestNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorTestTests {
    @Test
    public void testIsCompiledToFunction() {
        var node = TypedTestNode.builder()
            .name("f")
            .addBodyStatement(Typed.var("x", Typed.boolFalse()))
            .build();

        var result = PythonCodeGenerator.compileNamespaceStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                def test_f():
                    x = False
                """
        ));
    }

    @Test
    public void testNameIsCompiledToValidPythonIdentifier() {
        var node = TypedTestNode.builder()
            .name("one two three")
            .build();

        var result = PythonCodeGenerator.compileNamespaceStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                def test_one_two_three():
                    pass
                """
        ));
    }
}
