package org.zwobble.clunk.backends.java.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.codegenerator.JavaCodeGenerator;
import org.zwobble.clunk.backends.java.codegenerator.JavaCodeGeneratorContext;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class JavaMapMacroTests {
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

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("java.util.Optional.ofNullable(xs.get(key))"));
    }
}
