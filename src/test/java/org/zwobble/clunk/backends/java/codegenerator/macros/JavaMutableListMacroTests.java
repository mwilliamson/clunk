package org.zwobble.clunk.backends.java.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.codegenerator.JavaCodeGenerator;
import org.zwobble.clunk.backends.java.codegenerator.JavaCodeGeneratorContext;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaMutableListMacroTests {
    @Test
    public void mutableListConstructorCallIsCompiledToNewArrayList() {
        // TODO: missing type params
        var node = Typed.callConstructor(
            Typed.localReference(
                "MutableList",
                Types.metaType(Types.STRING)
            ),
            List.of(),
            Types.mutableList(Types.STRING)
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("new java.util.ArrayList<String>()"));
    }

    @Test
    public void methodsAreInheritedFromList() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.mutableList(Types.STRING)
            ),
            "length",
            List.of(),
            Types.INT
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.size()"));
    }

    @Test
    public void addCallIsCompiledToAdd() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.mutableList(Types.STRING)
            ),
            "add",
            List.of(Typed.string("")),
            Types.UNIT
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.add(\"\")"));
    }
}
