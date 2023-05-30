package org.zwobble.clunk.backends.typescript.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptCodeGenerator;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptCodeGeneratorContext;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class TypeScriptMapMacroTests {
    @Test
    public void getIsCompiledToGetCall() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.map(Types.STRING, Types.STRING)
            ),
            "get",
            List.of(Typed.localReference("key", Types.STRING)),
            Types.option(Types.STRING)
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.get(key) ?? null"));
    }
}
