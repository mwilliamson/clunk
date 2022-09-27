package org.zwobble.clunk.types;

import java.util.List;

public class OptionTypeConstructor implements TypeConstructor {
    public static final OptionTypeConstructor INSTANCE = new OptionTypeConstructor();

    private OptionTypeConstructor() {
    }

    @Override
    public List<TypeParameter> params() {
        return List.of(TypeParameter.covariant("T"));
    }

    @Override
    public String describe() {
        return "Option";
    }
}
