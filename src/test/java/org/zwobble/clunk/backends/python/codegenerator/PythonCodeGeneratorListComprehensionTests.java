package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.clunk.util.Serialisation.serialiseToString;
import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class PythonCodeGeneratorListComprehensionTests {
    @Test
    public void listComprehensionsAreCompiledToListComprehensions() {
        var node = Typed.listComprehension(
            List.of(
                Typed.comprehensionForClause(
                    "xs",
                    Types.list(Types.STRING),
                    Typed.localReference("xss", Types.list(Types.list(Types.STRING))),
                    List.of(
                        Typed.localReference("a", Types.BOOL),
                        Typed.localReference("b", Types.BOOL)
                    )
                ),
                Typed.comprehensionForClause(
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

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("[x for xs in xss if a if b for x in xs if c]"));
    }
}
