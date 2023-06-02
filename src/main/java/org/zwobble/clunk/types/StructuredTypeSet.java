package org.zwobble.clunk.types;

public class StructuredTypeSet implements TypeSet {
    public static final StructuredTypeSet INSTANCE = new StructuredTypeSet();

    private  StructuredTypeSet() {
    }

    @Override
    public String describe() {
        return "structured value";
    }
}
