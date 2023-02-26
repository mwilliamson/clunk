package org.zwobble.clunk.types;

import org.zwobble.clunk.ast.SourceType;

public record NamespaceId(NamespaceName name, SourceType sourceType) {
    public static NamespaceId source(NamespaceName name) {
        return new NamespaceId(name, SourceType.SOURCE);
    }

    public static NamespaceId source(String... parts) {
        return source(NamespaceName.fromParts(parts));
    }

    public static NamespaceId test(NamespaceName name) {
        return new NamespaceId(name, SourceType.TEST);
    }

    public static NamespaceId test(String... parts) {
        return test(NamespaceName.fromParts(parts));
    }

    @Override
    public String toString() {
        // TODO: include source type
        return name.toString();
    }
}
