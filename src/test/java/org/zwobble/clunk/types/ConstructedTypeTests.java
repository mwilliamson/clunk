package org.zwobble.clunk.types;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ConstructedTypeTests {
    @Test
    public void describeIncludesConstructorAndArgs() {
        var namespaceName = NamespaceName.fromParts();
        var constructor = new TypeConstructor(
            "Map",
            List.of(TypeParameter.invariant(namespaceName, "Map", "K"), TypeParameter.invariant(namespaceName, "Map", "V")),
            new RecordType(namespaceName, "Map")
        );
        var type = new ConstructedType(constructor, List.of(Types.STRING, Types.INT));

        var result = type.describe();

        assertThat(result, equalTo("Map[String, Int]"));
    }
}
