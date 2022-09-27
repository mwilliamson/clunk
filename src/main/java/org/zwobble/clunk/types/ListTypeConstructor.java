package org.zwobble.clunk.types;

import java.util.List;

public class ListTypeConstructor implements TypeConstructor {
    public static final ListTypeConstructor INSTANCE = new ListTypeConstructor();

    private ListTypeConstructor() {
    }

    @Override
    public Type call(List<Type> args) {
        // TODO: check args
        return new ConstructedType(this, List.of(args.get(0)));
    }

    @Override
    public String describe() {
        return "List";
    }
}
