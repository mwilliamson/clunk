package org.zwobble.clunk.types;

import java.util.List;

public record EnumType(
    NamespaceName namespaceName,
    String name,
    List<String> members
) implements Type {
    @Override
    public String describe() {
        return name;
    }

    @Override
    public Type replace(TypeMap typeMap) {
        return this;
    }
}
