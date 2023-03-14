package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserImportTypeTests {
    @Test
    public void canSerialiseImportOfType() {
        var node = Java.importType("com.example.User");

        var result = serialiseToString(node, JavaSerialiser::serialiseImport);

        assertThat(result, equalTo("import com.example.User;\n"));
    }
}
