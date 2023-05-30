package org.zwobble.clunk.backends.python.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGenerator;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGeneratorContext;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class PythonMapMacroTests {
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

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.get(key)"));
    }
}
