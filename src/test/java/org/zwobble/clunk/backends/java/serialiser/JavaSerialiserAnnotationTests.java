package org.zwobble.clunk.backends.java.serialiser;

import org.junit.jupiter.api.Test;
import org.zwobble.clunk.backends.java.ast.Java;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.zwobble.clunk.util.Serialisation.serialiseToString;

public class JavaSerialiserAnnotationTests {
    @Test
    public void canSerialiseMarkerAnnotation() {
        var node = Java.annotation(Java.typeReference("Test"));

        var result = serialiseToString(node, JavaSerialiser::serialiseAnnotation);

        assertThat(result, equalTo("@Test"));
    }

    @Test
    public void canSerialiseSingleElementAnnotation() {
        var node = Java.annotation(Java.typeReference("DisplayName"), Java.string("one two three"));

        var result = serialiseToString(node, JavaSerialiser::serialiseAnnotation);

        assertThat(result, equalTo("@DisplayName(\"one two three\")"));
    }
}
