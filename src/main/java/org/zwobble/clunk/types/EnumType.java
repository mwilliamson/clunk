package org.zwobble.clunk.types;

import java.util.List;

public record EnumType(String name, List<String> members) implements Type {
    @Override
    public String describe() {
        return name;
    }
}
