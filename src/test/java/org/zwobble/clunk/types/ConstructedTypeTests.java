package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ConstructedTypeTests {
    @Test
    public void describeIncludesConstructorAndArgs() {
        var namespaceName = NamespaceName.fromParts();
        var constructor = new TypeConstructor(
            "Map",
            List.of(TypeParameter.invariant(namespaceName, "Map", "K"), TypeParameter.invariant(namespaceName, "Map", "V")),
            Types.recordType(namespaceName, "Map")
        );
        var type = Types.construct(constructor, List.of(Types.STRING, Types.INT));

        var result = type.describe();

        assertThat(result, equalTo("Map[String, Int]"));
    }

    @Test
    public void replaceReplacesArgs() {
        var namespaceName = NamespaceName.fromParts();
        var constructor = new TypeConstructor(
            "Map",
            List.of(TypeParameter.invariant(namespaceName, "Map", "K"), TypeParameter.invariant(namespaceName, "Map", "V")),
            Types.recordType(namespaceName, "Map")
        );
        var typeParameter = TypeParameter.invariant(namespaceName, "Example", "T");
        var type = Types.construct(constructor, List.of(typeParameter));
        var typeMap = new TypeMap(Map.ofEntries(Map.entry(typeParameter, Types.STRING)));

        var result = type.replace(typeMap);

        assertThat(result, equalTo(Types.construct(constructor, List.of(Types.STRING))));
    }
}
