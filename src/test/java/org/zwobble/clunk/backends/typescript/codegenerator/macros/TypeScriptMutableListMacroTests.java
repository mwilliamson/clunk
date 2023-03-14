package org.zwobble.clunk.backends.typescript.codegenerator.macros;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptCodeGenerator;
import org.zwobble.clunk.backends.typescript.codegenerator.TypeScriptCodeGeneratorContext;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptMutableListMacroTests {
    @Test
    public void mutableListConstructorCallIsCompiledToEmptyList() {
        var node = Typed.callConstructor(
            Typed.localReference(
                "MutableList",
                Types.metaType(Types.STRING)
            ),
            List.of(Typed.typeLevelReference("String", Types.STRING)),
            List.of(),
            Types.mutableList(Types.STRING)
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("new Array<string>()"));
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

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs[42]"));
    }

    @Test
    public void addCallIsCompiledToPush() {
        var node = Typed.callMethod(
            Typed.localReference(
                "xs",
                Types.mutableList(Types.STRING)
            ),
            "add",
            List.of(Typed.string("")),
            Types.UNIT
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.push(\"\")"));
    }
}
