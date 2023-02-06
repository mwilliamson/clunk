package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorArgsTests {
    @Test
    public void positionalArgsAreCompiledToArgs() {
        var node = Typed.args(
            List.of(Typed.intLiteral(123), Typed.intLiteral(456)),
            List.of()
        );

        var result = PythonCodeGenerator.compileArgs(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseArgs);
        assertThat(string, equalTo("123, 456"));
    }

    @Test
    public void namedArgsAreCompiledToArgs() {
        var node = Typed.args(
            List.of(),
            List.of(
                Typed.namedArg("x", Typed.intLiteral(123)),
                Typed.namedArg("y", Typed.intLiteral(456))
            )
        );

        var result = PythonCodeGenerator.compileArgs(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseArgs);
        assertThat(string, equalTo("x=123, y=456"));
    }

    @Test
    public void namedArgsFollowPositionalArgs() {
        var node = Typed.args(
            List.of(Typed.intLiteral(123)),
            List.of(Typed.namedArg("y", Typed.intLiteral(456)))
        );

        var result = PythonCodeGenerator.compileArgs(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseArgs);
        assertThat(string, equalTo("123, y=456"));
    }
}
