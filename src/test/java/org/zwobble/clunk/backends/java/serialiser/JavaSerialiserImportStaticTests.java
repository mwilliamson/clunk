package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserImportStaticTests {
    @Test
    public void canSerialiseMarkerAnnotation() {
        var node = Java.importStatic("com.example", "assertThat");

        var result = serialiseToString(node, JavaSerialiser::serialiseImport);

        assertThat(result, equalTo("import static com.example.assertThat;\n"));
    }
}
