package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorForEachTests {
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

        var result = JavaCodeGenerator.compileFunctionStatement(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiser::serialiseStatements);
        assertThat(string, equalTo(
            """
            for (var x : xs) {
                "hello";
            }
            """));
    }
}
