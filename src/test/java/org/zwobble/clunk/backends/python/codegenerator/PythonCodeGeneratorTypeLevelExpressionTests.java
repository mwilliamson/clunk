package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.types.*;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorTypeLevelExpressionTests {
    @Test
    public void boolTypeIsCompiledToBoolType() {
        var node = Typed.typeLevelReference("Bool", BoolType.INSTANCE);
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("bool"));
    }

    @Test
    public void enumTypeIsCompiledToReference() {
        var node = Typed.typeLevelReference("C", Types.enumType(NamespaceId.source("a", "b"), "C", List.of()));
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void intTypeIsCompiledToIntType() {
        var node = Typed.typeLevelReference("Int", IntType.INSTANCE);
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("int"));
    }

    @Test
    public void interfaceTypeIsCompiledToReference() {
        var node = Typed.typeLevelReference("C", Types.interfaceType(NamespaceId.source("a", "b"), "C"));
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void listTypeIsCompiledToListType() {
        var node = Typed.constructedTypeInvariant(
            Typed.typeLevelReference("List", Types.LIST_CONSTRUCTOR),
            List.of(Typed.typeLevelReference("Int", Types.INT)),
            Types.list(Types.INT)
        );
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("typing.List[int]"));
    }

    @Test
    public void optionTypeIsCompiledToOptionalType() {
        var node = Typed.constructedTypeInvariant(
            Typed.typeLevelReference("Option", Types.OPTION_CONSTRUCTOR),
            List.of(Typed.typeLevelReference("Int", Types.INT)),
            Types.option(Types.INT)
        );
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("typing.Optional[int]"));
    }

    @Test
    public void recordTypeIsCompiledToReference() {
        var node = Typed.typeLevelReference("C", Types.recordType(NamespaceId.source("a", "b"), "C"));
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("C"));
    }

    @Test
    public void stringTypeIsCompiledToStrType() {
        var node = Typed.typeLevelReference("String", StringType.INSTANCE);
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.compileTypeLevelExpression(node, context);

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("str"));
    }
}
