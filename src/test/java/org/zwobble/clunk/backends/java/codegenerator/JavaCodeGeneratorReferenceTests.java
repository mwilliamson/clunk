package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.BoolType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorReferenceTests {
    @Test
    public void referenceIsCompiledToReference() {
        var node = Typed.reference("value", BoolType.INSTANCE);

        var result = JavaCodeGenerator.compileExpression(node);

        var string = serialiseToString(result, JavaSerialiser::serialiseExpression);
        assertThat(string, equalTo("value"));
    }
}
