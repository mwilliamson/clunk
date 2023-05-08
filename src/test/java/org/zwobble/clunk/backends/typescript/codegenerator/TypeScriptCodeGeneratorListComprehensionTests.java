package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class TypeScriptCodeGeneratorListComprehensionTests {
    @Test
    public void listComprehensionWithSingleForAndNoConditionsIsCompiledToMap() {
        var node = Typed.listComprehension(
            List.of(
                Typed.comprehensionIterable(
                    "x",
                    Types.INT,
                    Typed.localReference("xs", Types.list(Types.INT)),
                    List.of()
                )
            ),
            Typed.intAdd(Typed.localReference("x", Types.INT), Typed.intLiteral(1))
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("xs.map((x) => x + 1)"));
    }

    @Test
    @Disabled
    public void listComprehensionsAreCompiledToListComprehensions() {
        var node = Typed.listComprehension(
            List.of(
                Typed.comprehensionIterable(
                    "xs",
                    Types.list(Types.STRING),
                    Typed.localReference("xss", Types.list(Types.list(Types.STRING))),
                    List.of(
                        Typed.localReference("a", Types.BOOL),
                        Typed.localReference("b", Types.BOOL)
                    )
                ),
                Typed.comprehensionIterable(
                    "x",
                    Types.STRING,
                    Typed.localReference("xs", Types.list(Types.STRING)),
                    List.of(
                        Typed.localReference("c", Types.BOOL)
                    )
                )
            ),
            Typed.localReference("x", Types.STRING)
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("[x for xs in xss if a if b for x in xs if c]"));
    }
}
