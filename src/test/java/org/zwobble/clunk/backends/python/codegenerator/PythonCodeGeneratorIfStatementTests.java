package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorIfStatementTests {
    @Test
    public void ifStatementIsCompiledToPythonIfStatement() {
        var node = Typed.ifStatement(
            List.of(
                Typed.conditionalBranch(
                    Typed.reference("x", Types.BOOL),
                    List.of(Typed.returnStatement(Typed.intLiteral(42)))
                )
            ),
            List.of(
                Typed.returnStatement(Typed.intLiteral(47))
            )
        );

        var result = PythonCodeGenerator.compileFunctionStatement(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
            if x:
                return 42
            else:
                return 47
            """));
    }
}
