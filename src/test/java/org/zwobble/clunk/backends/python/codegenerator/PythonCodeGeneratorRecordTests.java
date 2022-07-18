package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedRecordNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.NamespaceName;

import java.util.List;

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

    @Test
    public void propertiesAreCompiledToProperties() {
        var node = TypedRecordNode.builder(NamespaceName.fromParts("example", "project"), "Example")
            .addProperty(Typed.property(
                "value",
                Typed.typeLevelString(),
                List.of(Typed.returnStatement(Typed.string("hello")))
            ))
            .build();
        var context = PythonCodeGeneratorContext.stub();

        var result = PythonCodeGenerator.DEFAULT.compileRecord(node, context);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                @((dataclasses).dataclass)(frozen=True)
                class Example:
                    @property
                    def value(self):
                        return "hello"
                """
        ));
    }
}
