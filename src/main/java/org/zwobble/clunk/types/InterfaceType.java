package org.zwobble.clunk.types;

import java.util.HashMap;

public record InterfaceType(NamespaceName namespaceName, String name) implements Type {
    @Override
    public String describe() {
        return namespaceName + "." + name;
    }

    @Override
    public Type replace(HashMap<TypeParameter, Type> typeMap) {
        return this;
    }
}
