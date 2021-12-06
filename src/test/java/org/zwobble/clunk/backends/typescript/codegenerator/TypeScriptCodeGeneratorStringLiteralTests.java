package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGenerator;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorStringLiteralTests {
    @Test
    public void stringIsCompiledToString() {
        var node = Typed.string("hello");

        var result = TypeScriptCodeGenerator.compileExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("\"hello\""));
    }
}
