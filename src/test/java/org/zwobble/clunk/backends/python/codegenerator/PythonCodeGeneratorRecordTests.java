package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.IntType;
import org.zwobble.clunk.types.StringType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorRecordTests {
    @Test
    public void recordIsCompiledToDataClass() {
        var node = TypedRecordNode.builder("Example")
            .addField(Typed.recordField("first", StringType.INSTANCE))
            .addField(Typed.recordField("second", IntType.INSTANCE))
            .build();

        var result = PythonCodeGenerator.DEFAULT.compileRecord(node);

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
