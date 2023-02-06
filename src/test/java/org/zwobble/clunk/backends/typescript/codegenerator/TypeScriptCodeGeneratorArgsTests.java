package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorArgsTests {
    @Test
    public void positionalArgsAreCompiledToArgs() {
        var node = Typed.args(
            List.of(Typed.intLiteral(123), Typed.intLiteral(456)),
            List.of()
        );

        var result = TypeScriptCodeGenerator.compileArgs(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseArgs);
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

        var result = TypeScriptCodeGenerator.compileArgs(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseArgs);
        assertThat(string, equalTo("123, 456"));
    }

    @Test
    public void namedArgsFollowPositionalArgs() {
        var node = Typed.args(
            List.of(Typed.intLiteral(123)),
            List.of(Typed.namedArg("y", Typed.intLiteral(456)))
        );

        var result = TypeScriptCodeGenerator.compileArgs(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseArgs);
        assertThat(string, equalTo("123, 456"));
    }
}
