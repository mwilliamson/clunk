package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorVarTests {
    @Test
    public void varIsCompiledToVariableDeclaration() {
        var node = Typed.var("x", Typed.boolFalse());

        var result = JavaCodeGenerator.compileFunctionStatement(node);

        var string = serialiseToString(result, JavaSerialiser::serialiseStatement);
        assertThat(string, equalTo("var x = false;\n"));
    }
}
