package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class NamespaceTypeTests {
    @Test
    public void describeReturnsNameOfNamespace() {
        var type = new NamespaceType(NamespaceId.source("Stdlib", "Assert"), Map.of());

        var result = type.describe();

        assertThat(result, equalTo("Stdlib/Assert"));
    }
}
