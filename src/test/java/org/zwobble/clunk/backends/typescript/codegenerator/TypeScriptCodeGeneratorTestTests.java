package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedTestNode;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorTestTests {
    @Test
    public void testIsCompiledToTestFunctionCall() {
        var node = TypedTestNode.builder()
            .name("can assign bool")
            .addBodyStatement(Typed.var("x", Typed.boolFalse()))
            .build();

        var result = TypeScriptCodeGenerator.compileNamespaceStatement(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
            test("can assign bool", function () {
                let x = false;
            });
            """
        ));
    }
}
