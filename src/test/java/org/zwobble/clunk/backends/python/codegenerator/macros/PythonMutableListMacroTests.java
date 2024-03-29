package org.zwobble.clunk.backends.python.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGenerator;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGeneratorContext;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonMutableListMacroTests {
    @Test
    public void mutableListConstructorCallIsCompiledToEmptyList() {
        // TODO: missing type params
        var node = Typed.callConstructor(
            Typed.localReference(
                "MutableList",
                Types.metaType(Types.STRING)
            ),
            List.of(),
            Types.mutableList(Types.STRING)
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("[]"));
    }

    @Test
    public void methodsAreInheritedFromList() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.mutableList(Types.STRING)
            ),
            "get",
            List.of(Typed.intLiteral(42)),
            Types.STRING
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs[42]"));
    }

    @Test
    public void addCallIsCompiledToAppend() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.mutableList(Types.STRING)
            ),
            "add",
            List.of(Typed.string("")),
            Types.UNIT
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.append(\"\")"));
    }
}
