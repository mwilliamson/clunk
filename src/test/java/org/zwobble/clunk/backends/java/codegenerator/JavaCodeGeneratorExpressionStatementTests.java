package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorExpressionStatementTests {
    @Test
    public void expressionStatementIsCompiledToExpressionStatement() {
        var node = Typed.expressionStatement(Typed.boolFalse());

        var result = JavaCodeGenerator.compileFunctionStatement(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiser::serialiseStatements);
        assertThat(string, equalTo("false;\n"));
    }
}
