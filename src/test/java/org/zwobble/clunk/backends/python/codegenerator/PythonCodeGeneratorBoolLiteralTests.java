package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorBoolLiteralTests {
    @Test
    public void falseLiteralGeneratesFalseLiteral() {
        var node = Typed.boolFalse();

        var result = PythonCodeGenerator.DEFAULT.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("False"));
    }

    @Test
    public void trueLiteralGeneratesTrueLiteral() {
        var node = Typed.boolTrue();

        var result = PythonCodeGenerator.DEFAULT.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("True"));
    }
}
