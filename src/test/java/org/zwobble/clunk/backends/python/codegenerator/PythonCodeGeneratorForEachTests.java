package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorForEachTests {
    @Test
    public void forEachIsCompiledToForEach() {
        var node = Typed.forEach(
            "x",
            Types.INT,
            Typed.localReference("xs", Types.list(Types.INT)),
            List.of(
                Typed.expressionStatement(Typed.string("hello"))
            )
        );

        var result = PythonCodeGenerator.compileFunctionStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatements);
        assertThat(string, equalTo(
            """
            for x in xs:
                "hello"
            """));
    }

    @Test
    public void targetIsConvertedToSnakeCase() {
        var node = Typed.forEach(
            "currentElement",
            Types.INT,
            Typed.localReference("xs", Types.list(Types.INT)),
            List.of()
        );

        var result = PythonCodeGenerator.compileFunctionStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatements);
        assertThat(string, equalTo(
            """
            for current_element in xs:
                pass
            """));
    }
}
