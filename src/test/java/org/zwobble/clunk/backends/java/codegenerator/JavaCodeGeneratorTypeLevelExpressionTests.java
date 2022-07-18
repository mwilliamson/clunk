package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorTypeLevelExpressionTests {
    @Test
    public void boolTypeIsCompiledToJavaBooleanType() {
        var node = Typed.typeLevelReference("Bool", BoolType.INSTANCE);
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("boolean"));
    }

    @Test
    public void enumTypeIsCompiledToReference() {
        var node = Typed.typeLevelReference("C", Types.enumType(NamespaceName.fromParts("a", "b"), "C", List.of()));
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void intTypeIsCompiledToJavaIntType() {
        var node = Typed.typeLevelReference("Int", IntType.INSTANCE);
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("int"));
    }

    @Test
    public void interfaceTypeIsCompiledToReference() {
        var node = Typed.typeLevelReference("C", Types.interfaceType(NamespaceName.fromParts("a", "b"), "C"));
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void listTypeIsCompiledToListType() {
        var node = Typed.constructedType(
            Typed.typeLevelReference("List", ListTypeConstructor.INSTANCE),
            List.of(Typed.typeLevelReference("String", Types.STRING)),
            Types.list(Types.STRING)
        );
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("java.util.List<String>"));
    }

    @Test
    public void optionTypeIsCompiledToOptionalType() {
        var node = Typed.constructedType(
            Typed.typeLevelReference("Option", OptionTypeConstructor.INSTANCE),
            List.of(Typed.typeLevelReference("STRING", Types.STRING)),
            Types.option(Types.STRING)
        );
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("java.util.Optional<String>"));
    }

    @Test
    public void recordTypeIsCompiledToReference() {
        var node = Typed.typeLevelReference("C", Types.recordType(NamespaceName.fromParts("a", "b"), "C"));
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void stringTypeIsCompiledToJavaStringType() {
        var node = Typed.typeLevelReference("String", StringType.INSTANCE);
        var context = JavaCodeGeneratorContext.stub();

        var result = JavaCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, JavaSerialiser::serialiseTypeExpression);
        assertThat(string, equalTo("String"));
    }
}
