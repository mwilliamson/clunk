package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.types.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorTypeLevelExpressionTests {
    @Test
    public void boolTypeIsCompiledToBooleanType() {
        var node = Typed.typeLevelReference("Bool", BoolType.INSTANCE);

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("boolean"));
    }

    @Test
    public void enumTypeIsCompiledToReference() {
        var node = Typed.typeLevelReference("C", Types.enumType(NamespaceName.fromParts("a", "b"), "C", List.of()));

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void intTypeIsCompiledToNumberType() {
        var node = Typed.typeLevelReference("Int", IntType.INSTANCE);

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("number"));
    }

    @Test
    public void interfaceTypeIsCompiledToReference() {
        var node = Typed.typeLevelReference("C", Types.interfaceType(NamespaceName.fromParts("a", "b"), "C"));

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void listTypeIsCompiledToArrayType() {
        var node = Typed.constructedType(
            Typed.typeLevelReference("List", ListTypeConstructor.INSTANCE),
            List.of(Typed.typeLevelReference("Int", Types.INT)),
            Types.list(Types.INT)
        );

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("(Array)<number>"));
    }

    @Test
    public void optionTypeIsCompiledToUnionWithNull() {
        var node = Typed.constructedType(
            Typed.typeLevelReference("Option", OptionTypeConstructor.INSTANCE),
            List.of(Typed.typeLevelReference("Int", Types.INT)),
            Types.option(Types.INT)
        );

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("number | null"));
    }

    @Test
    public void recordTypeIsCompiledToReference() {
        var node = Typed.typeLevelReference("C", Types.recordType(NamespaceName.fromParts("a", "b"), "C"));

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void stringTypeIsCompiledToStringType() {
        var node = Typed.typeLevelReference("String", StringType.INSTANCE);

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseExpression);
        assertThat(string, equalTo("string"));
    }
}
