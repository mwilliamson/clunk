package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorVarTests {
    @Test
    public void varIsCompiledToAssignment() {
        var node = Typed.var("x", Typed.boolFalse());

        var result = PythonCodeGenerator.compileFunctionStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatements);
        assertThat(string, equalTo("x = False\n"));
    }

    @Test
    public void namesArePythonized() {
        var node = Typed.var("isValid", Typed.boolFalse());

        var result = PythonCodeGenerator.compileFunctionStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatements);
        assertThat(string, equalTo("is_valid = False\n"));
    }
}
