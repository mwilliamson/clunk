package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorStringLiteralTests {
    @Test
    public void stringIsCompiledToString() {
        var node = Typed.string("hello");

        var result = PythonCodeGenerator.compileExpression(node);

        var string = serialiseToString(result, PythonSerialiser::serialiseExpression);
        assertThat(string, equalTo("\"hello\""));
    }
}
