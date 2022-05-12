package org.zwobble.clunk.types;

import java.util.Arrays;
import java.util.List;

public record NamespaceName(List<String> parts) {
    public static NamespaceName fromParts(String... parts) {
        return new NamespaceName(Arrays.asList(parts));
    }

    @Override
    public String toString() {
        return String.join("/", parts);
    }
}
