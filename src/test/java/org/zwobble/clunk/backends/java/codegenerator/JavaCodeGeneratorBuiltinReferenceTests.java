package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.Types;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorBuiltinReferenceTests {
    @Test
    public void whenContextIsNotGenericThenBoolIsCompiledToPrimitiveBoolean() {
        var value = Types.BOOL;

        var result = JavaCodeGenerator.builtinReference(value, false);

        var string = serialiseToString(result.orElseThrow(), JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("boolean"));
    }

    @Test
    public void whenContextIsGenericThenBoolIsCompiledToBoxedBoolean() {
        var value = Types.BOOL;

        var result = JavaCodeGenerator.builtinReference(value, true);

        var string = serialiseToString(result.orElseThrow(), JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("Boolean"));
    }

    @Test
    public void whenContextIsNotGenericThenIntIsCompiledToPrimitiveInt() {
        var value = Types.INT;

        var result = JavaCodeGenerator.builtinReference(value, false);

        var string = serialiseToString(result.orElseThrow(), JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("int"));
    }

    @Test
    public void whenContextIsGenericThenIntIsCompiledToBoxedInteger() {
        var value = Types.INT;

        var result = JavaCodeGenerator.builtinReference(value, true);

        var string = serialiseToString(result.orElseThrow(), JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("Integer"));
    }

    @Test
    public void stringIsCompiledToString() {
        var value = Types.STRING;

        var result = JavaCodeGenerator.builtinReference(value, true);

        var string = serialiseToString(result.orElseThrow(), JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("String"));
    }

    @Test
    public void optionIsCompiledToOptional() {
        var value = Types.OPTION_CONSTRUCTOR;

        var result = JavaCodeGenerator.builtinReference(value, true);

        var string = serialiseToString(result.orElseThrow(), JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("java.util.Optional"));
    }

    @Test
    public void typeMacrosAreUsedToCompileToJavaTypeReference() {
        var value = Types.STRING_BUILDER;

        var result = JavaCodeGenerator.builtinReference(value, true);

        var string = serialiseToString(result.orElseThrow(), JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("java.lang.StringBuilder"));
    }

    @Test
    public void typeConstructorMacrosAreUsedToCompileToJavaTypeReference() {
        var value = Types.LIST_CONSTRUCTOR;

        var result = JavaCodeGenerator.builtinReference(value, true);

        var string = serialiseToString(result.orElseThrow(), JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("java.util.List"));
    }
}
