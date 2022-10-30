package org.zwobble.clunk.types;

public interface StructuredType extends Type {
    NamespaceName namespaceName();
    String name();
    StructuredType replace(TypeMap typeMap);
}
