package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorTypeLevelExpressionTests {
    @Test
    public void boolTypeIsCompiledToBoolType() {
        var node = Typed.typeLevelExpression(BoolType.INSTANCE);
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.DEFAULT.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseExpression);
        assertThat(string, equalTo("bool"));
    }

    @Test
    public void intTypeIsCompiledToIntType() {
        var node = Typed.typeLevelExpression(IntType.INSTANCE);
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.DEFAULT.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseExpression);
        assertThat(string, equalTo("int"));
    }

    @Test
    public void interfaceTypeIsCompiledToReference() {
        var node = Typed.typeLevelExpression(Types.interfaceType(NamespaceName.fromParts("a", "b"), "C"));
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.DEFAULT.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void listTypeIsCompiledToListType() {
        var node = Typed.typeLevelExpression(Types.list(Types.INT));
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.DEFAULT.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseExpression);
        assertThat(string, equalTo("(typing).List[int]"));
    }

    @Test
    public void optionTypeIsCompiledToOptionalType() {
        var node = Typed.typeLevelExpression(Types.option(Types.INT));
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.DEFAULT.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseExpression);
        assertThat(string, equalTo("(typing).Optional[int]"));
    }

    @Test
    public void stringTypeIsCompiledToStrType() {
        var node = Typed.typeLevelExpression(StringType.INSTANCE);
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.DEFAULT.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseExpression);
        assertThat(string, equalTo("str"));
    }
}
