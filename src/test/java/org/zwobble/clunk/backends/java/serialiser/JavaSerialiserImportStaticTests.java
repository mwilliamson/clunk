package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserImportStaticTests {
    @Test
    public void canSerialiseStaticImport() {
        var node = Java.importStatic("com.example", "assertThat");

        var result = serialiseToString(node, JavaSerialiser::serialiseImport);

        assertThat(result, equalTo("import static com.example.assertThat;\n"));
    }
}
