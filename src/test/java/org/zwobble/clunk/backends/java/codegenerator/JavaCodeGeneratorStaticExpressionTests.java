package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorStaticExpressionTests {
    @Test
    public void boolTypeIsCompiledToJavaBooleanType() {
        var node = Typed.typeLevelExpression(BoolType.INSTANCE);
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("boolean"));
    }

    @Test
    public void intTypeIsCompiledToJavaIntType() {
        var node = Typed.typeLevelExpression(IntType.INSTANCE);
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("int"));
    }

    @Test
    public void stringTypeIsCompiledToJavaStringType() {
        var node = Typed.typeLevelExpression(StringType.INSTANCE);
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("String"));
    }
}
