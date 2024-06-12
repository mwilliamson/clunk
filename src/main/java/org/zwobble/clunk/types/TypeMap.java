package org.zwobble.clunk.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeMap {
    public static TypeMap from(List<TypeParam> params, List<? extends Type> args) {
        var typeMap = new HashMap<TypeParam, Type>();
        for (var i = 0; i < params.size(); i++) {
            var param = params.get(i);
            var arg = args.get(i);
            typeMap.put(param, arg);
        }
        return new TypeMap(typeMap);
    }

    private final Map<TypeParam, Type> typeMap;

    public TypeMap(Map<TypeParam, Type> typeMap) {
        this.typeMap = typeMap;
    }

    public Type get(TypeParam typeParam) {
        return typeMap.getOrDefault(typeParam, typeParam);
    }
}
