package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiser;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorIfStatementTests {
    @Test
    public void ifStatementIsCompiledToTypeScriptIfStatement() {
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

        var result = TypeScriptCodeGenerator.compileFunctionStatement(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiser::serialiseStatement);
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
