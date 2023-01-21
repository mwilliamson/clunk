package org.zwobble.clunk.backends.java.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.java.serialiser.JavaSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaCodeGeneratorMapLiteralTests {
    @Test
    public void mapsAreCompiledToCallToMapOfEntries() {
        var node = Typed.mapLiteral(
            List.of(
                Typed.mapEntryLiteral(Typed.intLiteral(1), Typed.intLiteral(2)),
                Typed.mapEntryLiteral(Typed.intLiteral(3), Typed.intLiteral(4))
            ),
            Types.INT,
            Types.INT
        );

        var result = JavaCodeGenerator.compileExpression(node, JavaCodeGeneratorContext.stub());

        var string = serialiseToString(result, JavaSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("java.util.Map.ofEntries(java.util.Map.entry(1, 2), java.util.Map.entry(3, 4))"));
    }
}
