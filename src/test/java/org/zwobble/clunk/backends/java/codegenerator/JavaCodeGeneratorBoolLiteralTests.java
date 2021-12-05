package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptCodeGenerator;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorBoolLiteralTests {
    @Test
    public void falseLiteralGeneratesFalseLiteral() {
        var node = Typed.boolFalse();

        var result = JavaCodeGenerator.compileExpression(node);

        var string = serialiseToString(result, JavaSerialiser::serialiseExpression);
        assertThat(string, equalTo("false"));
    }

    @Test
    public void trueLiteralGeneratesTrueLiteral() {
        var node = Typed.boolTrue();

        var result = JavaCodeGenerator.compileExpression(node);

        var string = serialiseToString(result, JavaSerialiser::serialiseExpression);
        assertThat(string, equalTo("true"));
    }
}
