package org.zwobble.clunk.types;

import java.util.List;

public class OptionTypeConstructor implements TypeConstructor {
    public static final OptionTypeConstructor INSTANCE = new OptionTypeConstructor();

    private OptionTypeConstructor() {
    }

    @Override
    public Type call(List<Type> args) {
        // TODO: check args
        return new OptionType(args.get(0));
    }

    @Override
    public String describe() {
        return "Option";
    }
}
