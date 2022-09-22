package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserExportTests {
    @Test
    public void canSerialiseExportOfOneName() {
        var node = TypeScript.export(List.of("a"));

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("export {a};\n"));
    }

    @Test
    public void canSerialiseExportOfMultipleNames() {
        var node = TypeScript.export(List.of("a", "b", "c"));

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("export {a, b, c};\n"));
    }
}
