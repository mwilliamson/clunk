package org.zwobble.clunk.types;

import java.util.Map;

public class TypeMap {
    private final Map<TypeParameter, Type> typeMap;

    public TypeMap(Map<TypeParameter, Type> typeMap) {
        this.typeMap = typeMap;
    }

    public Type get(TypeParameter typeParameter) {
        return typeMap.getOrDefault(typeParameter, typeParameter);
    }
}
