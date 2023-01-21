package org.zwobble.clunk.backends.python.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.python.serialiser.PythonSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class PythonCodeGeneratorMapLiteralTests {
    @Test
    public void mapsAreCompiledToDict() {
        var node = Typed.mapLiteral(
            List.of(
                Typed.mapEntryLiteral(Typed.intLiteral(1), Typed.intLiteral(2)),
                Typed.mapEntryLiteral(Typed.intLiteral(3), Typed.intLiteral(4))
            ),
            Types.INT,
            Types.INT
        );

        var result = PythonCodeGenerator.compileExpression(node, PythonCodeGeneratorContext.stub());

        var string = serialiseToString(result, PythonSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("{1: 2, 3: 4}"));
    }
}
