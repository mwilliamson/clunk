package org.zwobble.clunk.types;

public interface StructuredType extends Type {
    NamespaceId namespaceId();
    String name();
    StructuredType replace(TypeMap typeMap);
}
