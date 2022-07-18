package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorRecordTests {
    @Test
    public void recordIsCompiledToDataClass() {
        var node = TypedRecordNode.builder("Example")
            .addField(Typed.recordField("first", Typed.typeLevelString()))
            .addField(Typed.recordField("second", Typed.typeLevelInt()))
            .build();
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.DEFAULT.compileRecord(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                @((dataclasses).dataclass)(frozen=True)
                class Example:
                    first: str
                    second: int
                """
        ));
    }
}
