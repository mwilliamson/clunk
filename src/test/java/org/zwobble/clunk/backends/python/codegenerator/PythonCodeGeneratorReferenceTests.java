package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.BoolType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorReferenceTests {
    @Test
    public void referenceIsCompiledToReference() {
        var node = Typed.reference("value", BoolType.INSTANCE);

        var result = PythonCodeGenerator.DEFAULT.compileExpression(node);

        var string = serialiseToString(result, PythonSerialiser::serialiseExpression);
        assertThat(string, equalTo("value"));
    }
}
