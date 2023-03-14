package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.zwobble.precisely.AssertThat.assertThat;
import static org.zwobble.precisely.Matchers.equalTo;

public class ConstructedTypeTests {
    @Test
    public void describeIncludesConstructorAndArgs() {
        var namespaceId = NamespaceId.source();
        var constructor = new TypeConstructor(
            List.of(TypeParameter.invariant(namespaceId, "Map", "K"), TypeParameter.invariant(namespaceId, "Map", "V")),
            Types.recordType(namespaceId, "Map")
        );
        var type = Types.construct(constructor, List.of(Types.STRING, Types.INT));

        var result = type.describe();

        assertThat(result, equalTo("Map[String, Int]"));
    }

    @Test
    public void replaceReplacesArgs() {
        var namespaceId = NamespaceId.source();
        var constructor = new TypeConstructor(
            List.of(TypeParameter.invariant(namespaceId, "Map", "K"), TypeParameter.invariant(namespaceId, "Map", "V")),
            Types.recordType(namespaceId, "Map")
        );
        var typeParameter = TypeParameter.invariant(namespaceId, "Example", "T");
        var type = Types.construct(constructor, List.of(typeParameter));
        var typeMap = new TypeMap(Map.ofEntries(Map.entry(typeParameter, Types.STRING)));

        var result = type.replace(typeMap);

        assertThat(result, equalTo(Types.construct(constructor, List.of(Types.STRING))));
    }
}
