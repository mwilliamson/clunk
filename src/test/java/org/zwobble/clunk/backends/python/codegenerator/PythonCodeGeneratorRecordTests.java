package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.typechecker.FieldsLookup;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorRecordTests {
    @Test
    public void recordIsCompiledToDataClass() {
        var node = TypedRecordNode.builder("Example").build();
        var fieldsLookup = new FieldsLookup(Map.ofEntries(
            Map.entry(node.type(), List.of(
                Typed.recordField("first", Typed.typeLevelString()),
                Typed.recordField("second", Typed.typeLevelInt())
            ))
        ));
        var context = PythonCodeGeneratorContext.stub(fieldsLookup);

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
