package org.zwobble.clunk.backends.java.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.codegenerator.JavaCodeGenerator;
import org.zwobble.clunk.backends.java.codegenerator.JavaCodeGeneratorContext;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaListMacroTests {
    @Test
    public void containsIsCompiledToContainsCall() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "contains",
            List.of(Typed.localReference("x", Types.STRING)),
            Types.BOOL
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.contains(x)"));
    }

    @Test
    public void flatMapIsCompiledToStreamFlatMap() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "flatMap",
            List.of(Typed.localReference("f", Types.NOTHING)),
            Types.list(Types.STRING)
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.stream().map(f).flatMap(java.util.List::stream).toList()"));
    }

    @Test
    public void getCallIsCompiledToGetCall() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "get",
            List.of(Typed.intLiteral(42)),
            Types.STRING
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.get(42)"));
    }

    @Test
    public void lastCallIsCompiledToGetCall() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "last",
            List.of(),
            Types.STRING
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.get(xs.size() - 1)"));
    }

    @Test
    public void lengthCallIsCompiledToSizeCall() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.list(Types.STRING)
            ),
            "length",
            List.of(),
            Types.INT
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.size()"));
    }
}
