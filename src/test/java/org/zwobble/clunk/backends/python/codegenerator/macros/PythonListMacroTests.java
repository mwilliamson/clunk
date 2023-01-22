package org.zwobble.clunk.backends.python.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGenerator;
import org.zwobble.clunk.backends.python.codegenerator.PythonCodeGeneratorContext;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonListMacroTests {
    @Test
    public void containsIsCompiledToInOperation() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "contains",
            List.of(Typed.localReference("x", Types.STRING)),
            Types.BOOL
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("x in xs"));
    }

    @Test
    public void flatMapIsCompiledToListComprehension() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "flatMap",
            List.of(Typed.localReference("f", Types.NOTHING)),
            Types.STRING
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("[_result for _element in xs for _result in f(_element)]"));
    }

    @Test
    public void getIsCompiledToSubscription() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
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
    public void lastIsCompiledToSubscription() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "last",
            List.of(),
            Types.STRING
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs[-1]"));
    }

    @Test
    public void lengthIsCompiledToLenCall() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "length",
            List.of(),
            Types.INT
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("len(xs)"));
    }
}
