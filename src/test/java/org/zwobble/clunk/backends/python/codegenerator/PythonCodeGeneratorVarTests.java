package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorVarTests {
    @Test
    public void varIsCompiledToAssignment() {
        var node = Typed.var("x", Typed.boolFalse());

        var result = PythonCodeGenerator.compileFunctionStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo("x = False\n"));
    }
}
