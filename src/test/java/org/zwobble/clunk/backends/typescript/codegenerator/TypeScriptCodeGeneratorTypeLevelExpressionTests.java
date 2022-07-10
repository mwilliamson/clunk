package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.types.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorTypeLevelExpressionTests {
    @Test
    public void boolTypeIsCompiledToBooleanType() {
        var node = Typed.typeLevelExpression(BoolType.INSTANCE);

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("boolean"));
    }

    @Test
    public void intTypeIsCompiledToNumberType() {
        var node = Typed.typeLevelExpression(IntType.INSTANCE);

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("number"));
    }

    @Test
    public void interfaceTypeIsCompiledToReference() {
        var node = Typed.typeLevelExpression(Types.interfaceType(NamespaceName.fromParts("a", "b"), "C"));

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void listTypeIsCompiledToArrayType() {
        var node = Typed.typeLevelExpression(Types.list(Types.INT));

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("(Array)<number>"));
    }

    @Test
    public void optionTypeIsCompiledToUnionWithNull() {
        var node = Typed.typeLevelExpression(Types.option(Types.INT));

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("number | null"));
    }

    @Test
    public void recordTypeIsCompiledToReference() {
        var node = Typed.typeLevelExpression(Types.recordType(NamespaceName.fromParts("a", "b"), "C"));

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void stringTypeIsCompiledToStringType() {
        var node = Typed.typeLevelExpression(StringType.INSTANCE);

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("string"));
    }
}
