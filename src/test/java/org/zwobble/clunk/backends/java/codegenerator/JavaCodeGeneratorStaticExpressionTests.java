package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class JavaCodeGeneratorStaticExpressionTests {
    @Test
    public void boolTypeIsCompiledToJavaBooleanType() {
        var node = Typed.staticExpression(BoolType.INSTANCE);

        var result = JavaCodeGenerator.compileStaticExpression(node);

        var stringBuilder = new StringBuilder();
        JavaSerialiser.serialiseTypeReference(result, stringBuilder);
        assertThat(stringBuilder.toString(), equalTo("boolean"));
    }

    @Test
    public void intTypeIsCompiledToJavaIntType() {
        var node = Typed.staticExpression(IntType.INSTANCE);

        var result = JavaCodeGenerator.compileStaticExpression(node);

        var stringBuilder = new StringBuilder();
        JavaSerialiser.serialiseTypeReference(result, stringBuilder);
        assertThat(stringBuilder.toString(), equalTo("int"));
    }

    @Test
    public void stringTypeIsCompiledToJavaStringType() {
        var node = Typed.staticExpression(StringType.INSTANCE);

        var result = JavaCodeGenerator.compileStaticExpression(node);

        var stringBuilder = new StringBuilder();
        JavaSerialiser.serialiseTypeReference(result, stringBuilder);
        assertThat(stringBuilder.toString(), equalTo("String"));
    }
}
