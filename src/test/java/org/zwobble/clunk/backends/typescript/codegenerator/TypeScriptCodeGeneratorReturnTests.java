package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGenerator;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorReturnTests {
    @Test
    public void returnNodeGeneratesReturn() {
        var node = Typed.returnStatement(Typed.boolFalse());

        var result = TypeScriptCodeGenerator.compileFunctionStatement(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo("return false;\n"));
    }
}
