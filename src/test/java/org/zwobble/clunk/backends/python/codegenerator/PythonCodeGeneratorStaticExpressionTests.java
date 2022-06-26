package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorStaticExpressionTests {
    @Test
    public void boolTypeIsCompiledToJavaBooleanType() {
        var node = Typed.typeLevelExpression(BoolType.INSTANCE);

        var result = PythonCodeGenerator.DEFAULT.compileTypeLevelExpression(node);

        var string = serialiseToString(result, PythonSerialiser::serialiseExpression);
        assertThat(string, equalTo("bool"));
    }

    @Test
    public void intTypeIsCompiledToJavaIntType() {
        var node = Typed.typeLevelExpression(IntType.INSTANCE);

        var result = PythonCodeGenerator.DEFAULT.compileTypeLevelExpression(node);

        var string = serialiseToString(result, PythonSerialiser::serialiseExpression);
        assertThat(string, equalTo("int"));
    }

    @Test
    public void stringTypeIsCompiledToJavaStringType() {
        var node = Typed.typeLevelExpression(StringType.INSTANCE);

        var result = PythonCodeGenerator.DEFAULT.compileTypeLevelExpression(node);

        var string = serialiseToString(result, PythonSerialiser::serialiseExpression);
        assertThat(string, equalTo("str"));
    }
}
