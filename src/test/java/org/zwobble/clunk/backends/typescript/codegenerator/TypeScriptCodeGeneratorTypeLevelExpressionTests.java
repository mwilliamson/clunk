package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.*;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorTypeLevelExpressionTests {
    @Test
    public void boolTypeIsCompiledToBooleanType() {
        var node = Typed.typeLevelReference("Bool", BoolType.INSTANCE);

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("boolean"));
    }

    @Test
    public void enumTypeIsCompiledToReference() {
        var node = Typed.typeLevelReference("C", Types.enumType(NamespaceId.source("a", "b"), "C", List.of()));

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void intTypeIsCompiledToNumberType() {
        var node = Typed.typeLevelReference("Int", IntType.INSTANCE);

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("number"));
    }

    @Test
    public void interfaceTypeIsCompiledToReference() {
        var node = Typed.typeLevelReference("C", Types.interfaceType(NamespaceId.source("a", "b"), "C"));

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void optionTypeIsCompiledToUnionWithNull() {
        var node = Typed.constructedTypeInvariant(
            Typed.typeLevelReference("Option", Types.OPTION_CONSTRUCTOR),
            List.of(Typed.typeLevelReference("Int", Types.INT)),
            Types.option(Types.INT)
        );

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("number | null"));
    }

    @Test
    public void recordTypeIsCompiledToReference() {
        var node = Typed.typeLevelReference("C", Types.recordType(NamespaceId.source("a", "b"), "C"));

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void stringTypeIsCompiledToStringType() {
        var node = Typed.typeLevelReference("String", StringType.INSTANCE);

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("string"));
    }

    @Test
    public void typeMacrosAreUsedToCompileToTypeScriptTypeReference() {
        var node = Typed.typeLevelReference("Unit", UnitType.INSTANCE);

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("void"));
    }

    @Test
    public void typeConstructorMacrosAreUsedToCompileToTypeScriptTypeReference() {
        var node = Typed.constructedTypeInvariant(
            Typed.typeLevelReference("List", Types.LIST_CONSTRUCTOR),
            List.of(Typed.typeLevelReference("String", Types.STRING)),
            Types.list(Types.STRING)
        );

        var result = TypeScriptCodeGenerator.compileTypeLevelExpression(node);

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("ReadonlyArray<string>"));
    }
}
