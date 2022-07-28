package org.zwobble.clunk.typechecker;

import org.zwobble.clunk.sources.Source;
import org.zwobble.clunk.types.Type;

import java.util.HashMap;
import java.util.Map;

public class MemberTypesBuilder {
    private final Map<String, Type> memberTypes = new HashMap<>();

    public void add(String name, Type type, Source source) {
        if (memberTypes.containsKey(name)) {
            throw new FieldIsAlreadyDefinedError(name, source);
        }

        memberTypes.put(name, type);
    }

    public void addAll(Map<String, Type> memberTypes, Source source) {
        for (var entry : memberTypes.entrySet()) {
            add(entry.getKey(), entry.getValue(), source);
        }
    }

    public Map<String, Type> build() {
        return memberTypes;
    }
}
