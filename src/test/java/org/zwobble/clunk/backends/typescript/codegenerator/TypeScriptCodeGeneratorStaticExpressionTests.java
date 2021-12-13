package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.types.BoolType;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorStaticExpressionTests {
    @Test
    public void boolTypeIsCompiledToJavaBooleanType() {
        var node = Typed.staticExpression(BoolType.INSTANCE);

        var result = TypeScriptCodeGenerator.compileStaticExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("boolean"));
    }

    @Test
    public void intTypeIsCompiledToJavaIntType() {
        var node = Typed.staticExpression(IntType.INSTANCE);

        var result = TypeScriptCodeGenerator.compileStaticExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("number"));
    }

    @Test
    public void stringTypeIsCompiledToJavaStringType() {
        var node = Typed.staticExpression(StringType.INSTANCE);

        var result = TypeScriptCodeGenerator.compileStaticExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("string"));
    }
}
