package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.Types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorTypeLevelValueToTypeExpressionTests {
    @Test
    public void referencesToBuiltinsAreCompiledToAppropriateReference() {
        var value = Types.BOOL;
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.typeLevelValueToTypeExpression(value, false, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("boolean"));
    }
}
