package org.zwobble.clunk.backends.typescript.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptCodeGenerator;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptCodeGeneratorContext;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptListMacroTests {
    @Test
    public void flatMapIsCompiledToFlatMapCall() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "flatMap",
            List.of(Typed.localReference("f", Types.NOTHING)),
            Types.STRING
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.flatMap(f)"));
    }

    @Test
    public void getIsCompiledToIndex() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "get",
            List.of(Typed.intLiteral(42)),
            Types.STRING
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs[42]"));
    }

    @Test
    public void lastIsCompiledToIndex() {
        // TODO: use lodash, this is unsafe due to side effects
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "last",
            List.of(),
            Types.STRING
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs[xs.length - 1]"));
    }

    @Test
    public void lengthIsCompiledToLengthAccess() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "length",
            List.of(),
            Types.INT
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.length"));
    }
}