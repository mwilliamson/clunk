package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorForEachTests {
    @Test
    public void forEachIsCompiledToForOf() {
        var node = Typed.forEach(
            "x",
            Types.INT,
            Typed.localReference("xs", Types.list(Types.INT)),
            List.of(
                Typed.expressionStatement(Typed.string("hello"))
            )
        );

        var result = TypeScriptCodeGenerator.compileFunctionStatement(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatements);
        assertThat(string, equalTo(
            """
            for (let x of xs) {
                "hello";
            }
            """));
    }
}
