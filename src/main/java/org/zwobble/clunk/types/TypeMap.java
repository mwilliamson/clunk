package org.zwobble.clunk.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeMap {
    public static TypeMap from(List<TypeParameter> params, List<Type> args) {
        var typeMap = new HashMap<TypeParameter, Type>();
        for (var i = 0; i < params.size(); i++) {
            var param = params.get(i);
            var arg = args.get(i);
            typeMap.put(param, arg);
        }
        return new TypeMap(typeMap);
    }

    private final Map<TypeParameter, Type> typeMap;

    public TypeMap(Map<TypeParameter, Type> typeMap) {
        this.typeMap = typeMap;
    }

    public Type get(TypeParameter typeParameter) {
        return typeMap.getOrDefault(typeParameter, typeParameter);
    }
}
