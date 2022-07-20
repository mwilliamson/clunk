package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiser;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorIfStatementTests {
    @Test
    public void ifStatementIsCompiledToJavaIfStatement() {
        var node = Typed.ifStatement(
            List.of(
                Typed.conditionalBranch(
                    Typed.referenceVariable("x", Types.BOOL),
                    List.of(Typed.returnStatement(Typed.intLiteral(42)))
                )
            ),
            List.of(
                Typed.returnStatement(Typed.intLiteral(47))
            )
        );

        var result = JavaCodeGenerator.compileFunctionStatement(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
            if (x) {
                return 42;
            } else {
                return 47;
            }
            """));
    }
}
