package org.zwobble.clunk.backends.typescript.codegenerator;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.ast.typed.Typed;
import org.zwobble.clunk.backends.typescript.serialiser.TypeScriptSerialiserTesting;
import org.zwobble.clunk.types.Types;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptCodeGeneratorMapLiteralTests {
    @Test
    public void mapsAreCompiledToMaps() {
        var node = Typed.mapLiteral(
            List.of(
                Typed.mapEntryLiteral(Typed.intLiteral(1), Typed.intLiteral(2)),
                Typed.mapEntryLiteral(Typed.intLiteral(3), Typed.intLiteral(4))
            ),
            Types.INT,
            Types.INT
        );

        var result = TypeScriptCodeGenerator.compileExpression(node, TypeScriptCodeGeneratorContext.stub());

        var string = serialiseToString(result, TypeScriptSerialiserTesting::serialiseExpression);
        assertThat(string, equalTo("new Map([[1, 2], [3, 4]])"));
    }
}
