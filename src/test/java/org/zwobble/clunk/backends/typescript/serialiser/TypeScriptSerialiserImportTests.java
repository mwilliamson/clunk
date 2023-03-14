package org.zwobble.clunk.backends.typescript.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.typescript.ast.TypeScript;

import java.util.List;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class TypeScriptSerialiserImportTests {
    @Test
    public void canSerialiseImportOfOneExport() {
        var node = TypeScript.import_("numbers", List.of(
            TypeScript.importNamedMember("one")
        ));

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("import {one} from \"numbers\";\n"));
    }

    @Test
    public void canSerialiseImportOfMultipleExports() {
        var node = TypeScript.import_("numbers", List.of(
            TypeScript.importNamedMember("one"),
            TypeScript.importNamedMember("two")
        ));

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("import {one, two} from \"numbers\";\n"));
    }

    @Test
    public void canRenameImportedMember() {
        var node = TypeScript.import_("numbers", List.of(
            TypeScript.importNamedMember("one", "numbers_one")
        ));

        var result = serialiseToString(node, TypeScriptSerialiser::serialiseStatement);

        assertThat(result, equalTo("import {one as numbers_one} from \"numbers\";\n"));
    }
}
