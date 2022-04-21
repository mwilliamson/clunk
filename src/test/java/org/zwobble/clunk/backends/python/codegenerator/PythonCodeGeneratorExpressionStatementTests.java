package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorExpressionStatementTests {
    @Test
    public void expressionStatementGeneratesExpressionStatement() {
        var node = Typed.expressionStatement(Typed.boolFalse());

        var result = PythonCodeGenerator.DEFAULT.compileFunctionStatement(node);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo("False\n"));
    }
}
