package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.ast.typed.TypedCallNode;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiser;
import org.zwobble.clunk.builtins.Builtins;
import org.zwobble.clunk.sources.NullSource;
import org.zwobble.clunk.types.StaticFunctionType;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorExpressionStatementTests {
    @Test
    public void expressionStatementGeneratesExpressionStatement() {
        var node = Typed.expressionStatement(Typed.boolFalse());

        var result = PythonCodeGenerator.DEFAULT.compileFunctionStatement(node);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo("False\n"));
    }

    @Test
    public void staticAssertCallsAreCompiledToAssertStatements() {
        var node = Typed.expressionStatement(new TypedCallNode(
            Typed.reference(
                "isTrue",
                new StaticFunctionType(
                    Builtins.NAMESPACE_STDLIB_ASSERT.name(),
                    "isTrue",
                    List.of(Types.BOOL),
                    Types.UNIT
                )
            ),
            List.of(Typed.boolFalse()),
            Types.UNIT,
            NullSource.INSTANCE
        ));

        var result = PythonCodeGenerator.DEFAULT.compileFunctionStatement(node);

        var string = serialiseToString(result, PythonSerialiser::serialiseStatement);
        assertThat(string, equalTo(
            """
                assert False
                """
        ));
    }
}
